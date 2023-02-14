package com.developlife.reviewtwits;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MockTests {

    @DisplayName("일부러 성공하고, Github Action 의 gradlew 가 성공하는지 확인하는 테스트")
    @Test
    void mockTestForCICD(){
        int x = 1;
        assertThat(x).isEqualTo(1);
    }
}
