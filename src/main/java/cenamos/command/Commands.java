package cenamos.command;

import static java.util.Arrays.stream;

public enum Commands {
    NO_OP(""),
    ADD_PLACE("agregarLugar"),
    LIST_PLACES("lugares"),
    ECHO("echo")
    ;

    private final String commandName;

    Commands(String commandName) {
        this.commandName = commandName;
    }

    public static Commands getCommandFor(String text) {
        return stream(Commands.values())
                .filter(command -> command.commandName.equalsIgnoreCase(text))
                .findFirst()
                .orElse(NO_OP);
    }
}
