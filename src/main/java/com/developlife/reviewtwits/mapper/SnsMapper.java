package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.entity.Review;
import com.developlife.reviewtwits.message.response.review.ReactionResponse;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.sns.ItemResponse;
import com.developlife.reviewtwits.message.response.sns.SearchAllResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.util.StreamUtils;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {ReviewMapper.class})
public interface SnsMapper {
    ItemResponse toItemResponse(ItemDetail itemDetail);

    @Named(value = "toSearchAllResponse")
    default SearchAllResponse toSearchAllResponse(List<ItemDetail> itemDetailList, List<Review> reviewList, List<List<ReactionResponse>> reactionResponsesList) {
        SearchAllResponse.SearchAllResponseBuilder builder = SearchAllResponse.builder();
        List<ItemResponse> itemResponseList = itemDetailList.stream()
            .map(itemDetail -> toItemResponse(itemDetail)).toList();
        builder.itemList(itemResponseList);

        List<DetailSnsReviewResponse> detailSnsReviewResponseList = StreamUtils.zip(
            reviewList.stream(),
            reactionResponsesList.stream(),
            (review, reactionResponses) -> ReviewMapper.INSTANCE.toDetailSnsReviewResponse(review, reactionResponses)
        ).toList();
        builder.reviewList(detailSnsReviewResponseList);


        return builder.build();

    }
}
