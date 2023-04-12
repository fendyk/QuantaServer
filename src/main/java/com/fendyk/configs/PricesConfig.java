package com.fendyk.configs;

import com.fendyk.utilities.Log;
import com.fendyk.utilities.PayableCommand;
import de.leonhard.storage.Toml;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PricesConfig {

    Toml config;
    double teleportCommandPrice;

    HashMap<String, Integer> commandIndexes;
    HashMap<Integer, PayableCommand> commands;

    public PricesConfig() {
        initialize();
    }

    public void initialize() {
        commandIndexes = new HashMap<>();
        commands = new HashMap<>();

        config = new Toml("prices", "plugins/QuantaServer");

        teleportCommandPrice = config.getOrSetDefault("teleportCommandPrice", 1D);

        if (config.get("commands") == null) {
            config.setDefault("commands.spawn.command", "/gm");
            config.setDefault("commands.spawn.aliases", new String[]{"gamemode"});
            config.setDefault("commands.spawn.price", 1D);
            config.setDefault("commands.spawn.timeInSeconds", 30D);
        }

        AtomicInteger index = new AtomicInteger(0);
        config.getMap("commands").forEach((name, data) -> {
            Log.info("Adding registered payed command '" + name.toString() + "' to the server.");
            Log.info(data.toString());

            int currIndex = index.getAndIncrement();

            String command = config.getString("commands." + name + ".command");

            List<String> indexAliases = config.getStringList("commands." + name + ".aliases");
            ArrayList<String> newAliases = new ArrayList<>();

            for (String o : indexAliases) {
                String alias = "/" + o;
                Log.info("Adding alias: " + alias + " for " + command);
                newAliases.add(alias);
            }

            double price = config.getDouble("commands." + name + ".price");
            long time = config.getLong("commands." + name + ".timeInSeconds");

            Log.info("Index set to: '" + currIndex);
            PayableCommand payableCommand = new PayableCommand(
                    command,
                    newAliases,
                    price,
                    time,
                    0
            );

            commandIndexes.put(command, currIndex);
            commands.put(currIndex, payableCommand);
        });
    }

    public double getTeleportCommandPrice() {
        return teleportCommandPrice;
    }

    /**
     * Check if command exists. Returns 0 or greater if found. Integer is the id for the command.
     * Returns -1 if not found.
     *
     * @param commandName
     * @param args
     * @return
     */
    public int getCommandIndex(String commandName, String[] args) {

        Optional<Map.Entry<String, Integer>> str = commandIndexes.entrySet().stream().filter(item -> {
            String[] commandParts = item.getKey().split(" ");
            int index = item.getValue();
            String cmdName = commandParts[0];
            String[] cmdArgs = Arrays.copyOfRange(commandParts, 1, commandParts.length);

            // Check if the number of arguments match
            if (args.length != cmdArgs.length) {
                return false;
            }

            // Iterate over the command arguments and compare with the expected arguments
            for (int i = 0; i < cmdArgs.length; i++) {
                String cmdArg = cmdArgs[i];
                String arg = args[i];

                // Check if the argument is enclosed within angle brackets
                if (cmdArg.startsWith("<") && cmdArg.endsWith(">")) {
                    // Argument is a placeholder, skip comparison
                    continue;
                }

                // Check if the argument matches the expected argument
                if (!cmdArg.equalsIgnoreCase(arg)) {
                    // Argument does not match, command does not match
                    return false;
                }
            }

            // Check if the command name or any of its aliases match
            if (commandName.equalsIgnoreCase(cmdName)) {
                return true;
            } else {
                ArrayList<String> aliasList = commands.get(index).getAliases();
                return aliasList != null && aliasList.contains(commandName);
            }
        }).findFirst();

        if (str.isPresent()) {
            return str.get().getValue();
        }

        return -1;
    }

    public PayableCommand getCommandByIndex(int index) {
        return commands.get(index);
    }

}
