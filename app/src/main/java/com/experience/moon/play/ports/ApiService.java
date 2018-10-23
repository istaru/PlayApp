package com.experience.moon.play.ports;

import com.experience.moon.play.params.request.FirstReqDto;
import com.experience.moon.play.params.response.FirstResDto;
import com.moon.lib.request.UrlConfig;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST(UrlConfig.STAET_APP)
    Observable<FirstResDto> startApp(@Body FirstReqDto reqDto);

    @POST(UrlConfig.USER_AACTIVATION)
    Observable<FirstResDto> activationUser(@Body FirstReqDto reqDto);
}
