package ua.sl.igor.MyCasino.controllers;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import ua.sl.igor.MyCasino.DTO.ExceptionDTO;
import ua.sl.igor.MyCasino.DTO.RouletteChatMessageDTO;
import ua.sl.igor.MyCasino.domain.Player;
import ua.sl.igor.MyCasino.util.exceptions.MessageIsEmptyException;

@Controller
public class ChatController {

    @MessageMapping("/roulette/chat")
    @SendTo("/topic/chat")
    public RouletteChatMessageDTO sendMsgToChat(@RequestBody String msg, Authentication authentication) {
        Player player = (Player) authentication.getPrincipal();
        if (msg.trim().equals("")) {
            throw new MessageIsEmptyException();
        }
        return new RouletteChatMessageDTO(msg.trim(), player.getName());
    }

    @MessageExceptionHandler({MethodArgumentNotValidException.class, MessageIsEmptyException.class})
    @SendToUser("/topic/console")
    public ExceptionDTO handleMethodArgumentNotValidException() {
        return new ExceptionDTO("Message in chat can't be empty!", 400);
    }

}
