package code.name.monkey.retromusic.ui.fragments.mainactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.velitasali.music.R;

import java.util.ArrayList;

import code.name.monkey.backend.Injection;
import code.name.monkey.backend.model.Playlist;
import code.name.monkey.backend.model.smartplaylist.HistoryPlaylist;
import code.name.monkey.backend.model.smartplaylist.LastAddedPlaylist;
import code.name.monkey.backend.model.smartplaylist.MyTopTracksPlaylist;
import code.name.monkey.backend.mvp.contract.PlaylistContract;
import code.name.monkey.backend.mvp.presenter.PlaylistPresenter;
import code.name.monkey.retromusic.ui.adapter.PlaylistAdapter;
import code.name.monkey.retromusic.ui.fragments.base.AbsLibraryPagerRecyclerViewFragment;
import code.name.monkey.retromusic.util.PreferenceUtil;


public class PlaylistFragment extends AbsLibraryPagerRecyclerViewFragment<PlaylistAdapter, LinearLayoutManager> implements PlaylistContract.PlaylistView
{
	private PlaylistPresenter presenter;

	public static PlaylistFragment newInstance()
	{
		Bundle args = new Bundle();
		PlaylistFragment fragment = new PlaylistFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		presenter = new PlaylistPresenter(Injection.provideRepository(getContext()), this);
	}

	@Override
	protected LinearLayoutManager createLayoutManager()
	{
		return new LinearLayoutManager(getActivity());
	}

	@NonNull
	@Override
	protected PlaylistAdapter createAdapter()
	{
		return new PlaylistAdapter((AppCompatActivity) getActivity(), new ArrayList<>(), R.layout.item_list, null);
	}

	@Override
	public void setMenuVisibility(boolean menuVisible)
	{
		super.setMenuVisibility(menuVisible);
		if (menuVisible)
			getLibraryFragment().getToolbar().setTitle(PreferenceUtil.getInstance(getContext()).tabTitles() ? R.string.library : R.string.playlists);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getLibraryFragment().getToolbar().setTitle(PreferenceUtil.getInstance(getContext()).tabTitles() ? R.string.library : R.string.playlists);
		if (getAdapter().getDataSet().isEmpty()) presenter.subscribe();
	}

	@Override
	public void onDestroy()
	{
		presenter.unsubscribe();
		super.onDestroy();
	}

	@Override
	public void onMediaStoreChanged()
	{
		super.onMediaStoreChanged();
		presenter.loadPlaylists();
	}

	@Override
	public void loading()
	{
		getProgressBar().setVisibility(View.VISIBLE);
	}

	@Override
	public void showEmptyView()
	{
		getAdapter().swapDataSet(new ArrayList<>());
	}

	@Override
	public void completed()
	{
		getProgressBar().setVisibility(View.GONE);
	}

	@Override
	public void showData(ArrayList<Playlist> customPlayList)
	{
		ArrayList<Playlist> playList = new ArrayList<>();

		playList.add(new HistoryPlaylist(getContext()));
		playList.add(new LastAddedPlaylist(getContext()));
		playList.add(new MyTopTracksPlaylist(getContext()));

		playList.addAll(customPlayList);

		getAdapter().swapDataSet(playList);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.action_shuffle_all);
		menu.removeItem(R.id.action_sort_order);
		menu.removeItem(R.id.action_grid_size);
	}

	@Override
	protected int getEmptyMessage()
	{
		return R.string.no_playlists;
	}
}
