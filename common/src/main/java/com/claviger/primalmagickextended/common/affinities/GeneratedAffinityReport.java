package com.claviger.primalmagickextended.common.affinities;

import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities.Generated;
import com.claviger.primalmagickextended.common.affinities.GeneratedAffinities.Skipped;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.verdantartifice.primalmagick.common.affinities.AbstractAffinity;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LiquidBlock;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Writes the report of a generation run, for reviewing the rules, and the generated entries in Primal Magick's data
 * pack format, for copying into a data pack.
 */
public final class GeneratedAffinityReport {
    private static final Logger LOGGER = LogUtils.getLogger();

    private GeneratedAffinityReport() {}

    static void write(GeneratedAffinities.Run run, Path dir, long millis) {
        try {
            Files.createDirectories(dir);
            Files.writeString(dir.resolve(GeneratedAffinities.REPORT_FILE), markdown(run, millis), StandardCharsets.UTF_8);
            JsonArray entries = new JsonArray();
            run.generated().values().forEach(generated ->
                    entries.add(AbstractAffinity.dispatchCodec().encodeStart(JsonOps.INSTANCE, generated.affinity()).getOrThrow()));
            Files.writeString(dir.resolve(GeneratedAffinities.DATA_FILE), new GsonBuilder().setPrettyPrinting().create().toJson(entries), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Could not write the generated affinity report to {}", dir, e);
        }
    }

    private static String markdown(GeneratedAffinities.Run run, long millis) {
        StringBuilder out = new StringBuilder();
        out.append("# Generated affinities\n\n");
        out.append("Generated ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19))
                .append(" in ").append(millis).append(" ms: ").append(run.generated().size()).append(" generated, ")
                .append(run.skipped().size()).append(" blocks skipped, ").append(run.notConsidered())
                .append(" other items without an affinity not considered (not blocks, potted plants, fluids).\n\n");
        out.append("Affinity = analog (the vanilla item a role is based on) plus context modifiers. Each addition raises a source to the ")
                .append("modifier unit, the analog's largest amount.\n\n");

        Map<String, List<Generated>> byNamespace = new TreeMap<>();
        run.generated().values().forEach(generated -> byNamespace.computeIfAbsent(generated.item().getNamespace(), $ -> new ArrayList<>()).add(generated));
        for (Map.Entry<String, List<Generated>> entry : byNamespace.entrySet()) {
            out.append("## Generated: ").append(entry.getKey()).append(" (").append(entry.getValue().size()).append(")\n\n");
            out.append("| Item | Affinity | Derivation | Role | Evidence |\n|---|---|---|---|---|\n");
            entry.getValue().stream().sorted((a, b) -> a.item().compareTo(b.item())).forEach(generated -> {
                String derivation = generated.base() == null ? "set" : "`" + generated.base().getPath() + "`";
                if (!generated.modifiers().isEmpty()) {
                    derivation += " " + String.join("; ", generated.modifiers());
                }
                out.append("| `").append(generated.item().getPath()).append("`")
                        .append(generated.secondPass() ? " (recipe gave nothing)" : "")
                        .append(" | ").append(sources(generated.total()))
                        .append(" | ").append(cell(derivation))
                        .append(" | ").append(GeneratedAffinities.name(generated.role())).append(": ").append(cell(generated.roleReason()))
                        .append(" | ").append(cell(generated.evidence())).append(" |\n");
            });
            out.append('\n');
        }

        if (!run.skipped().isEmpty()) {
            out.append("## Skipped blocks (").append(run.skipped().size()).append(")\n\n");
            out.append("| Item | Reason | Evidence |\n|---|---|---|\n");
            run.skipped().stream().sorted((a, b) -> a.item().compareTo(b.item())).forEach(skipped ->
                    out.append("| `").append(skipped.item()).append("` | ").append(cell(skipped.reason())).append(" | ").append(cell(skipped.evidence())).append(" |\n"));
            out.append('\n');
        }

        // What the rest of each mod's blocks get from Primal Magick's recipe scan once these are in place
        Set<String> namespaces = new TreeSet<>(byNamespace.keySet());
        run.skipped().forEach(skipped -> namespaces.add(skipped.item().getNamespace()));
        namespaces.remove(Identifier.DEFAULT_NAMESPACE);
        for (String namespace : namespaces) {
            List<String> derived = new ArrayList<>();
            List<String> empty = new ArrayList<>();
            Set<Identifier> skippedIds = run.skipped().stream().map(Skipped::item).collect(Collectors.toSet());
            BuiltInRegistries.ITEM.keySet().stream().filter(id -> id.getNamespace().equals(namespace)).sorted().forEach(id -> {
                if (run.generated().containsKey(id) || skippedIds.contains(id)) {
                    return;
                }
                Item item = BuiltInRegistries.ITEM.getValue(id);
                if (item instanceof BlockItem blockItem && (blockItem.getBlock() instanceof FlowerPotBlock || blockItem.getBlock() instanceof LiquidBlock)) {
                    return;
                }
                SourceList total = run.currentTotal(id);
                if (total.isEmpty()) {
                    empty.add("`" + id.getPath() + "`" + (item instanceof BlockItem ? "" : " (item)"));
                } else {
                    derived.add("`" + id.getPath() + "` " + sources(total));
                }
            });
            out.append("## Other ").append(namespace).append(" items\n\n");
            out.append("From Primal Magick's data or recipe scan (").append(derived.size()).append("):\n\n");
            derived.forEach(line -> out.append("- ").append(line).append('\n'));
            out.append("\nStill without an affinity (").append(empty.size()).append("): ").append(String.join(", ", empty)).append("\n\n");
        }
        return out.toString();
    }

    /** A source list as text, e.g. "earth 10, sun 10", in Primal Magick's source order. */
    static String sources(SourceList list) {
        if (list.isEmpty()) {
            return "none";
        }
        return list.getSourcesSorted().stream().map(source -> source.getId().getPath() + " " + list.getAmount(source)).collect(Collectors.joining(", "));
    }

    private static String cell(String text) {
        return text.replace("|", "\\|").replace("\n", " ");
    }
}
