package com.zxj.music.fusion;
import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import com.zxj.music.fusion.*;
import com.zxj.music.fusion.task.*;
import com.zxj.music.fusion.util.*;
import com.zxj.music.fusion.widget.*;

public class SearchActivity extends Activity
{

	private SearchView searchView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View layout = getLayoutInflater().inflate(R.layout.activity_search, null);
		setContentView(layout);
		searchView = (SearchView) findViewById(R.id.search_view);
		setupSearchView();

	}


	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.searchback:
				sendKeyword(null);
				break;
		}
	}

	@Override
	public void onEnterAnimationComplete()
	{
		searchView.requestFocus();
		ImeUtils.showIme(searchView);
		super.onEnterAnimationComplete();
	}


	public void sendKeyword(String keyword)
	{
		ImeUtils.hideIme(searchView);
		if (keyword != null)
		{
			Intent i=new Intent();
			i.putExtra("keyword", keyword);
			setResult(1, i);}
		finishAfterTransition();
	}

	private void setupSearchView()
	{
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);

		searchView.setQuery(TaskUtil.keyword, false);
		searchView.setSubmitButtonEnabled(true);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
								 EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(final String query)
				{
					ImeUtils.hideIme(searchView);

					sendKeyword(query);
					
					return true;
				}

				@Override
				public boolean onQueryTextChange(String query)
				{
					return true;
				}
			});

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus)
				{
					if (!hasFocus)
					{

						Toast.makeText(SearchActivity.this, "再次点击返回", 0).show();
					}
				}
			});

	}


}
