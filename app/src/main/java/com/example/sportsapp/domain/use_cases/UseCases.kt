package com.example.sportsapp.domain.use_cases

import javax.inject.Inject

data class UseCases @Inject constructor(
    val getSportEventList: GetSportEventListUseCase,
    val getJoinedSportEventList: GetJoinedSportEventListUseCase,
    val addSportEvent: AddSportEventUseCase,
    val removeSportEvent: RemoveSportEventUseCase,
    val joinSportEventUseCase: JoinSportEventUseCase
)