package com.example.nytcodingchallenge.recycler_view;

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

import com.example.nytcodingchallenge.R;
import com.example.nytcodingchallenge.model.Organization;

import java.util.ArrayList;
import java.util.List;

public class RepositoryViewsAdapter extends RecyclerView.Adapter<RepositoryViewsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Organization> dataSet;
    private static OnClickListener clickListener;

    /*
    RepositoryViewsAdapter(Context context, ArrayList<Organization> organizations){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        setDataSet(organizations);
    }


    private void setDataSet(ArrayList<Organization> organizations) {
        this.dataSet = organizations;
    }

     */

    RepositoryViewsAdapter(ArrayList<Organization> organizations){
        dataSet = organizations;
        //dataSet = new ArrayList<>();
    }

    public void setAdapterList(List<Organization> organizations){
        dataSet.clear();
        dataSet.addAll(organizations);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            repoLayout = itemView.findViewById(R.id.repo_card_layout);
            companyName = itemView.findViewById(R.id.repo_company_display);
            repoName = itemView.findViewById(R.id.repo_name_display);
            starCount = itemView.findViewById(R.id.repo_stars_display);
            webView = itemView.findViewById(R.id.repo_main_web_view);
            itemView.setOnClickListener(this);
        }

        private void setWebViewSettings() {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }

        public void setOrg(Organization org){
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
