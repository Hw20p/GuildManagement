package com.example.royal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DiscordMessageService {

    /**
     * 디스코드 메시지를 사용자별로 분류하여 반환
     */
    public static Map<String, List<String>> parseDiscordMessagesByUser(String discordText) {
        Map<String, List<String>> userMessages = new LinkedHashMap<>();

        if (discordText == null || discordText.trim().isEmpty()) {
            return userMessages;
        }

        // 각 메시지를 라인별로 분리
        String[] lines = discordText.split("\n");
        String currentUser = null;
        StringBuilder currentMessage = new StringBuilder();

        for (String line : lines) {
            line = line.trim();

            // 날짜 구분선 스킵
            if (line.matches("\\d{4}년.*") || line.isEmpty()) {
                continue;
            }

            // 새로운 메시지 시작 패턴: "번호. 닉네임 — 날짜시간"
            Pattern messageStartPattern = Pattern.compile("^(?:\\d+\\.\\s+)?([^—]+?)\\s+—\\s+(.+)$");
            Matcher matcher = messageStartPattern.matcher(line);

            if (matcher.find()) {
                // 이전 메시지가 있으면 저장
                if (currentUser != null && currentMessage.length() > 0) {
                    userMessages.computeIfAbsent(currentUser, k -> new ArrayList<>())
                            .add(currentMessage.toString().trim());
                }

                // 새 사용자와 메시지 시작
                currentUser = cleanUsername(matcher.group(1));
                currentMessage = new StringBuilder();
            } else {
                // 메시지 내용 추가
                if (currentMessage.length() > 0) {
                    currentMessage.append("\n");
                }
                currentMessage.append(line);
            }
        }

        // 마지막 메시지 저장
        if (currentUser != null && currentMessage.length() > 0) {
            userMessages.computeIfAbsent(currentUser, k -> new ArrayList<>())
                    .add(currentMessage.toString().trim());
        }

        return userMessages;
    }

    /**
     * 사용자명에서 특수문자 제거
     */
    private static String cleanUsername(String username) {
        if (username == null) return "";

        // 특수문자, 이모지, 괄호 내용 제거
        return username.replaceAll("[•ᴗ*\\(\\)연습생]", "").trim();
    }
}