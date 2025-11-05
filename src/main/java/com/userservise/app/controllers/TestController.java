package com.userservise.app.controllers;

import com.userservise.app.model.dto.CardDto;
import com.userservise.app.model.dto.UserDto;
import com.userservise.app.model.entity.Card;
import com.userservise.app.repository.CardRepository;
import com.userservise.app.service.CardService;
import com.userservise.app.service.UserService;
import com.userservise.app.utils.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final CardService cardService;
    private final UserService userService;

    private final CardRepository cardRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable(name = "id") Integer userId
    ) {

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/card/{id}")
    public ResponseEntity<CardDto> getCardById(
            @PathVariable(name = "id") Integer id
    ) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @PostMapping("/card/add/{userId}")
    public ResponseEntity<CardDto> addCard(
            @PathVariable(name = "userId") Integer userId
    ) {
        return ResponseEntity.ok(cardService.createCard(userId));
    }
}
