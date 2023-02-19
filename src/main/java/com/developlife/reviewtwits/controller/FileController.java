package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.message.request.FileUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    @PostMapping("/files/save")
    public ResponseEntity<Message> saveFile(@ModelAttribute FileUpdateRequest request){




        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
