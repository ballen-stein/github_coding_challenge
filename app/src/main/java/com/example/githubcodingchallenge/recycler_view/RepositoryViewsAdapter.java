package com.example.githubcodingchallenge.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.githubcodingchallenge.R;
import com.example.githubcodingchallenge.model.Organization;

import java.util.ArrayList;

public class RepositoryViewsAdapter extends RecyclerView.Adapter<RepositoryViewsAdapter.ViewHolder> {

    private ArrayList<Organization> dataSet;
    private static OnClickListener clickListener;

    RepositoryViewsAdapter(ArrayList<Organization> organizations){
        dataSet = organizations;
    }


    @NonNull
    @Override
    public RepositoryViewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_cardview, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RepositoryViewsAdapter.ViewHolder holder, final int position) {
        final Organization organization = dataSet.get(position);
        TextView companyName = holder.companyName;
        TextView repoName = holder.repoName;
        TextView starCount = holder.starCount;

        CardView repoLayout = holder.repoLayout;
        repoLayout.setContentDescription(dataSet.get(position).getHtml_url());

        String[] repoDetails = dataSet.get(position).getFull_name().split("/", 2);
        companyName.setText(repoDetails[0]);
        repoName.setText(repoDetails[1]);
        starCount.setText(String.valueOf(dataSet.get(position).getStargazers_count()));

        holder.setOrg(organization);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView companyName, repoName, starCount;
        CardView repoLayout;
        WebView webView;
        Organization organization;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            repoLayout = itemView.findViewById(R.id.repo_card_layout);
            companyName = itemView.findViewById(R.id.repo_company_display);
            repoName = itemView.findViewById(R.id.repo_name_display);
            starCount = itemView.findViewById(R.id.repo_stars_display);
            webView = itemView.findViewById(R.id.repo_main_web_view);
            itemView.setOnClickListener(this);
        }


        private void setOrg(Organization org){
            this.organization = org;
        }


        @Override
        public void onClick(View view) {
            clickListener.onRepoClick(getAdapterPosition(), organization, view);
        }
    }


    public void setOnClickListener(OnClickListener cListener){
        RepositoryViewsAdapter.clickListener = cListener;
    }


    public interface OnClickListener {
        void onRepoClick(int position, Organization organization, View v);
    }
}
