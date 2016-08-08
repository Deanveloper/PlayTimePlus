package com.deanveloper.playtime.exporter;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Export to a plaintext file
 *
 * @author Dean B
 */
public class PlainTextExporter extends Exporter {
    @Override
    protected void exportFile(List<String> names, List<UUID> ids, List<Integer> secondsOnline) {
        List<String> formatted = new ArrayList<>(names.size());
        for (int i = 0; i < names.size(); i++) {
            formatted.add(String.format("%s (aka %s) has been on for %s",
                    ids.get(i),
                    names.get(i),
                    Utils.format(secondsOnline.get(i))
            ));
        }

        try {
            Files.write(
                    Paths.get(PlayTime.getInstance().getDataFolder().getAbsolutePath(), getFileName() + ".txt"),
                    formatted
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
