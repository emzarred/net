package com.pc.netapp.Utils;

import com.pc.netapp.Models.GithubUser;
import com.pc.netapp.Models.GithubUserInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;



public class GithubStreams {

    public static Observable<List<GithubUser>> streamFetchUserFollowing(String username){
        GithubService gitHubService = GithubService.retrofit.create(GithubService.class);
        return gitHubService.getFollowing(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<GithubUserInfo> streamFetchUserInfos(String username){
        GithubService gitHubService = GithubService.retrofit.create(GithubService.class);
        return gitHubService.getUserInfos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<GithubUserInfo> streamFetchUserFollowingAndFetchFirstUserInfos(String username){
        return streamFetchUserFollowing(username)
                .map(new Function<List<GithubUser>, GithubUser>() {
                    @Override
                    public GithubUser apply(List<GithubUser> users) throws Exception {
                        return users.get(0);
                    }
                })
                .flatMap(new Function<GithubUser, Observable<GithubUserInfo>>() {
                    @Override
                    public Observable<GithubUserInfo> apply(GithubUser user) throws Exception {
                        return streamFetchUserInfos(user.getLogin());
                    }
                });
    }
}

