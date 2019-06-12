package ru.ystu.myystu.Adapters;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.ystu.myystu.AdaptersData.AboutLicensesData;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.StringFormatter;

public class AboutLicensesAdapter extends RecyclerView.Adapter<AboutLicensesAdapter.AboutLicensesViewHolder> {

    private ArrayList<AboutLicensesData> mList;

    static class AboutLicensesViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView title;
        private final AppCompatTextView text;

        AboutLicensesViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemLic_title);
            text = itemView.findViewById(R.id.itemLic_text);
        }
    }

    public AboutLicensesAdapter(ArrayList<AboutLicensesData> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public AboutLicensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout_about_lic_item, parent, false);
        return new AboutLicensesViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutLicensesViewHolder holder, int position) {
        holder.title.setText(mList.get(position).getTitle());

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mList.get(position).getText());
        spannableStringBuilder = new StringFormatter().getUrlLink(spannableStringBuilder);
        spannableStringBuilder = new StringFormatter().getEmail(spannableStringBuilder);
        holder.text.setText(spannableStringBuilder);
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
