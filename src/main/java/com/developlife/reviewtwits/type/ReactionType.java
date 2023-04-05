package com.developlife.reviewtwits.type;

import com.developlife.reviewtwits.entity.Reaction;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ReactionType {

    LOVE("\uD83D\uDE0D"),
    SUNGLASSES("\uD83D\uDE0E"),
    LAUGHING("\uD83E\uDD23"),
    SURPRISING("\uD83D\uDE32"),
    THINKING("\uD83E\uDD14"),
    PLEADING("\uD83E\uDD7A"),
    SHOCKING("\uD83E\uDEE2"),
    PRAYING("\uD83D\uDE4F"),
    GOOD("\uD83D\uDC4D"),
    NOTICING("\uD83D\uDC40");

    private String emoji;

    ReactionType(String emoji) {
    }
    
    public static Map<String, ReactionResponse> classifyReactionResponses(User user,List<Reaction> reactionList){
        ReactionType userReactionType = null;
        Map<ReactionType, Integer> reactionClassify = new HashMap<>();
        for(Reaction reaction : reactionList){
            try{
                ReactionType reactionType = reaction.getReactionType();
                Integer reactionCount = reactionClassify.get(reactionType);
                reactionClassify.replace(reactionType, reactionCount + 1);
            }catch(NullPointerException ex){
                reactionClassify.put(reaction.getReactionType(), 1);
            }

            if(reaction.getUser().equals(user)){
                userReactionType = reaction.getReactionType();
            }
        }
        return mappingReactionResponse(userReactionType,reactionClassify);
    }

    private static Map<String, ReactionResponse> mappingReactionResponse
            (ReactionType userReactionType,Map<ReactionType, Integer> reactionClassify){

        Map<String, ReactionResponse> reactionMap = new HashMap<>();
        reactionClassify.forEach((reactionType, count) -> {
            reactionMap.put(reactionType.toString(),ReactionResponse.builder()
                    .isReacted(reactionType == userReactionType)
                    .count(count)
                    .build());
        });
        return reactionMap;
    }
}
