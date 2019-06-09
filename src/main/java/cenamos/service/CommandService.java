package cenamos.service;

import cenamos.MyLogger;
import cenamos.command.CommandParser;
import cenamos.command.Commands;
import cenamos.model.Message;
import cenamos.model.Update;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class CommandService {

    private static final MyLogger logger = MyLogger.logger(CommandService.class);

    private final CommandParser parser;
    private final Map<Commands, Consumer<Message>> commandHandler = new HashMap<>();

    public CommandService(BotService botService, CommandParser parser) {
        this.parser = parser;

        commandHandler.put(Commands.ECHO, botService::echo);
        commandHandler.put(Commands.NO_OP, botService::noOp);
        commandHandler.put(Commands.ADD_PLACE, botService::addPlace);
        commandHandler.put(Commands.LIST_PLACES, botService::listPlaces);
    }


    public void resolveCommand(Update update) {
        Message message = update.getMessage();

        if (message == null) {
            logger.info("Update {} came with no message.", update.getUpdateId());
            return;
        }

        String messageText = message.getText() != null ? message.getText() : message.getCaption();
        if (messageText == null) {
            logger.info("Update {} came with no message or caption text.", update.getUpdateId());
            return;
        }

        String commandRequest = messageText.split(" ")[0];
        if (!parser.isValidCommand(commandRequest)) {
            return;
        }

        Commands command = parser.parseCommand(commandRequest);
        commandHandler.get(command).accept(message);
    }

}
