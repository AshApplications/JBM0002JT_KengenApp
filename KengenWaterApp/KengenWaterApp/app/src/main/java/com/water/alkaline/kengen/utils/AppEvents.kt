package com.water.alkaline.kengen.utils

import com.water.alkaline.kengen.model.feedback.Feedback

data class NewFeedBackEvent(
    val model: Feedback,
)

data class FeedBacksEvent(
    val feedbacks: List<Feedback>,
)

