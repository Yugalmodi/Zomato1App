package com.techpalle.zomatoapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebFrag extends Fragment {
    WebView webView;

    public WebFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_web, container, false);
        webView = (WebView) v.findViewById(R.id.web_view);
        Bundle b = getArguments();
        String name = b.getString("name");
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if(homeActivity.internetConnection()){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("http://google.co.in/search?q="+name);
        }
        return v;
    }

}
