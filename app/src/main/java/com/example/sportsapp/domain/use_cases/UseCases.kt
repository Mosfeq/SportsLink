package com.example.sportsapp.domain.use_cases

import javax.inject.Inject

data class UseCases @Inject constructor(
    val register: RegisterUseCase,
    val signIn: SignInUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val getSportEventList: GetSportEventListUseCase,
    val getHostedSportEventList: GetHostedSportEventListUseCase,
    val getJoinedSportEventList: GetJoinedSportEventListUseCase,
    val addSportEvent: AddSportEventUseCase,
    val removeSportEvent: RemoveSportEventUseCase,
    val joinSportEventUseCase: JoinSportEventUseCase
)