package com.developlife.reviewtwits.mapper;

import com.developlife.reviewtwits.entity.ItemDetail;
import com.developlife.reviewtwits.message.response.sns.DetailSnsReviewResponse;
import com.developlife.reviewtwits.message.response.sns.ItemResponse;
import com.developlife.reviewtwits.message.response.sns.SearchAllResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {ReviewMapper.class})
public interface SnsMapper {

    @Named(value = "toItemResponse")
    @Mapping(target = "productName", source = "relatedProduct.name")
    @Mapping(target = "productImageUrl", source = "relatedProduct.imagePath")
    @Mapping(target = "url", source = "relatedProduct.productUrl")
    ItemResponse toItemResponse(ItemDetail itemDetail);

    @Named(value = "toSearchAllResponse")
    default SearchAllResponse toSearchAllResponse(List<ItemDetail> itemDetailList, List<DetailSnsReviewResponse> detailSnsReviewResponseList) {
        SearchAllResponse.SearchAllResponseBuilder builder = SearchAllResponse.builder();
        List<ItemResponse> itemResponseList = itemDetailList.stream()
            .map(itemDetail -> toItemResponse(itemDetail)).toList();
        builder.itemList(itemResponseList);
        builder.reviewList(detailSnsReviewResponseList);
        return builder.build();

    }
}
