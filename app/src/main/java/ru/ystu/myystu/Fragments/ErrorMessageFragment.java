package ru.ystu.myystu.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.ystu.myystu.R;

public class ErrorMessageFragment extends Fragment {

    private AppCompatTextView messageTextView;
    private AppCompatImageView iconImageView;
    private ContentFrameLayout refreshBtn;

    private String msg;
    private int code;
    private int view_id;
    private int backFragment_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            msg = getArguments().getString("error_msg");
            code = getArguments().getInt("error_code");
            view_id = getArguments().getInt("view");
            backFragment_id = getArguments().getInt("backFragment");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshBtn.setOnClickListener(view -> {

            if(getActivity().getSupportFragmentManager() != null){
                final Fragment backFragment = getActivity().getSupportFragmentManager().findFragmentById(backFragment_id);
                if (backFragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(view_id, backFragment).commit();
                }
            }

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO Обработка ошибок
        switch (code){
            // Отсутсвует подключение к интернету
            case 0:
                if(msg == null)
                    messageTextView.setText(getResources().getString(R.string.error_message_internet_error));
                else
                    messageTextView.setText(msg);

                iconImageView.setImageResource(R.drawable.ic_connect_error);
                break;
            // Файл не найден
            case 1:
                if(msg == null)
                    messageTextView.setText(getResources().getString(R.string.error_message_file_not_found));
                else
                    messageTextView.setText(msg);

                iconImageView.setImageResource(R.drawable.ic_file_not_found);
                break;
            // Остальные
            default:
                if(msg == null){
                    messageTextView.setVisibility(View.GONE);
                } else {
                    if(messageTextView.getVisibility() == View.GONE)
                        messageTextView.setVisibility(View.VISIBLE);

                    messageTextView.setText(msg);
                }

                iconImageView.setImageResource(R.drawable.ic_error);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.fragment_error_message, container, false);
        messageTextView = mView.findViewById(R.id.error_message_msg);
        iconImageView = mView.findViewById(R.id.error_message_icon);
        refreshBtn = mView.findViewById(R.id.error_message_refresh_btn);
        return mView;

    }

}
