package cenamos.service;

import cenamos.MyLogger;
import cenamos.connector.TelegramConnector;
import cenamos.model.Message;
import cenamos.model.SendMessage;
import cenamos.model.SendPhoto;
import cenamos.repository.PlacesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BotService {

    private static final MyLogger logger = MyLogger.logger(BotService.class);

    private static final int PERSON_SHRUG_EMOJI = 129335;
    private static final int OK_HAND_EMOJI = 128076;
    private static final int THUMBS_DOWN_EMOJI = 128078;

    private static final String NO_REPLY_MESSAGE = "Que cosa?";
    private static final String EMPTY_REPO_MESSAGE = "No tengo imagenes jaja salu2";

    private final TelegramConnector connector;
    private final PlacesRepository repository;

    public BotService(TelegramConnector connector, @Qualifier("Filesystem") PlacesRepository repository) {
        this.connector = connector;
        this.repository = repository;
    }

    public void noOp(Message message) {
        logger.debug("No operation for message {}", message.getMessageId());
    }

    public void addPlace(Message message) {
        String place = message.getText().split(" ")[0];

        if (place == null || "".equals(place)) {
            connector.sendMessage(createReply(message, createEmoji(PERSON_SHRUG_EMOJI)));
            return;
        }

        boolean persisted = repository.put(place);
        if (persisted) {
            connector.sendMessage(createReply(message, createEmoji(OK_HAND_EMOJI)));
        } else {
            connector.sendMessage(createReply(message, createEmoji(THUMBS_DOWN_EMOJI)));
        }
    }

    public void listPlaces(Message message){
        List<String> allPlaces = repository.getAll();

        if (CollectionUtils.isEmpty(allPlaces)) {
            connector.sendMessage(createReply(message, createEmoji(PERSON_SHRUG_EMOJI)));
            return;
        }

        String response = allPlaces.stream().collect(Collectors.joining("\n", "-", ""));
        connector.sendMessage(createReply(message, response));
    }

    public void echo(Message message) {
        SendMessage sendMessage;
        try {
            ObjectMapper mapper = new ObjectMapper();
            sendMessage = createReply(message, "Message: " + mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            sendMessage = createReply(message, "Error reading that thingy");
        }
        connector.sendMessage(sendMessage);
    }

    private SendMessage createReply(Message message, String text) {
        Long id = message.getChat().getId();
        SendMessage sendMessage = new SendMessage(String.valueOf(id), text);
        sendMessage.setDisableNotification(true);
        return sendMessage;
    }

    private String createEmoji(Integer code) {
        char[] emoji = Character.toChars(code);
        return new String(emoji);
    }

    public void check(Message message) {
        if (message.getReplyToMessage() == null) {
            connector.sendMessage(createReply(message, NO_REPLY_MESSAGE));
            return;
        }
        if (repository.isEmpty()) {
            connector.sendMessage(createReply(message, EMPTY_REPO_MESSAGE));
            return;
        }

        String checkImage = repository.getAny();
        connector.sendImage(createPhotoReply(message, checkImage));
    }

    private SendPhoto createPhotoReply(Message message, String fileId) {
        Long id = message.getChat().getId();
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(id), fileId);
        sendPhoto.setDisableNotification(true);
        sendPhoto.setReplyToMessageId(message.getReplyToMessage().getMessageId());
        return sendPhoto;
    }
}
