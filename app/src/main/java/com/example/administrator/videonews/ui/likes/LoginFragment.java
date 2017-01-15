package com.example.administrator.videonews.ui.likes;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.administrator.videonews.R;
import com.example.administrator.videonews.bombapi.BombClient;
import com.example.administrator.videonews.bombapi.UserApi;
import com.example.administrator.videonews.bombapi.result.UserResult;
import com.example.administrator.videonews.commons.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment {

    private Unbinder unbinder;

    @BindView(R.id.etUsername)
    TextInputEditText etUsername;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //无标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnLogin)
    public void onclick(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        //用户名和密码不能为空
        if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }
        //显示进度条
        btnLogin.setVisibility(View.GONE);

        //登录网络请求
        UserApi userApi = BombClient.getInstance().getUserApi();
        Call<UserResult> call = userApi.login(username,password);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                //隐藏进度条
                btnLogin.setVisibility(View.VISIBLE);
                //登陆未成功
                if (!response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
