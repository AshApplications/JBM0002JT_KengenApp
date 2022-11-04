package com.water.alkaline.kengen.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.preference.PowerPreference;
import com.water.alkaline.kengen.R;
import com.water.alkaline.kengen.data.db.viewmodel.AppViewModel;
import com.water.alkaline.kengen.databinding.ActivityHomeBinding;
import com.water.alkaline.kengen.databinding.DialogInfoBinding;
import com.water.alkaline.kengen.library.toprightmenu.MenuItem;
import com.water.alkaline.kengen.library.toprightmenu.TopRightMenu;
import com.water.alkaline.kengen.model.main.Category;
import com.google.gms.ads.InterAds;
import com.google.gms.ads.MainAds;
import com.water.alkaline.kengen.ui.adapter.DrawerCatAdapter;
import com.water.alkaline.kengen.ui.adapter.ViewPagerFragmentAdapter;
import com.water.alkaline.kengen.ui.fragment.BannerFragment;
import com.water.alkaline.kengen.ui.fragment.ChannelFragment;
import com.water.alkaline.kengen.ui.fragment.PdfFragment;
import com.water.alkaline.kengen.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    public TopRightMenu menu;
    public List<MenuItem> menuItems;
    public AppViewModel viewModel;
    ActivityHomeBinding binding;
    String[] strings = new String[100];
    DrawerCatAdapter adapter;
    boolean isOpen = false;
    ViewPagerFragmentAdapter pagerFragmentAdapter;
    Bundle savedState;

    @Override
    protected void onResume() {
        super.onResume();
        new MainAds().showBannerAds(this, binding.includedAd.adFrameMini, binding.includedAd.adSpaceMini);
    }

    public void setBG() {
        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    public void setup(Activity activity, View view) {

        menu = new TopRightMenu(activity);

        menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(R.mipmap.info, "Disclaimer"));
        menuItems.add(new MenuItem(R.mipmap.feedback, "Feedback"));
        menuItems.add(new MenuItem(R.mipmap.share, "Share App"));
        menuItems.add(new MenuItem(R.mipmap.thumb, "Rate Us"));

        menu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .showIcon(true)
                .dimBackground(true)
                .needAnimationStyle(true)
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {

                        switch (position) {
                            case 0:
                                showInfoDialog();
                                break;
                            case 1:
                                new InterAds().showInterAds(HomeActivity.this, new InterAds.OnAdClosedListener() {
                                    @Override
                                    public void onAdClosed() {
                                        startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
                                    }
                                });
                                break;
                            case 2:
                                shareApp();
                                break;
                            case 3:
                                rateUs();
                                break;
                        }
                        menu.dismiss();
                    }
                });

        menu.showAsDropDown(view, 0, 0);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        savedState = savedInstanceState;
        Context context;

        setBG();
        if (PowerPreference.getDefaultFile().getBoolean("mCheckFirst", true)) {
            PowerPreference.getDefaultFile().putBoolean("mCheckFirst", false);
            showInfoDialog();
        }
        listener();
        setTabs();
    }

    public void rateUs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));


            String sAux = PowerPreference.getDefaultFile().getString(Constant.appShareMsg, "");

            String sAux2 = "https://play.google.com/store/apps/details?id=" + getPackageName();
            sAux = sAux + "\n\n" + sAux2;
            i.putExtra(Intent.EXTRA_TEXT, sAux);

            startActivity(Intent.createChooser(i, "Choose One"));
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showToast(HomeActivity.this, "Something went wrong");
        }

    }

    private void listener() {
        binding.ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.drawer.isDrawerOpen(GravityCompat.START))
                    binding.drawer.openDrawer(GravityCompat.START);
                else
                    binding.drawer.closeDrawer(GravityCompat.START);
            }
        });

        binding.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup(HomeActivity.this, binding.ivInfo);
            }
        });

        binding.vpHome.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.navView.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                if (item.getItemId() < 10000) {
                    binding.navView.setCheckedItem(item.getItemId());
                    binding.vpHome.setCurrentItem(item.getOrder());
                    binding.drawer.closeDrawer(GravityCompat.START);
                } else {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    if (item.getItemId() >= 10001 && item.getItemId() <= 10003) {
                        new InterAds().showInterAds(HomeActivity.this, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                if (item.getItemId() == 10001) {
                                    startActivity(new Intent(HomeActivity.this, SaveActivity.class));
                                } else if (item.getItemId() == 10002) {
                                    startActivity(new Intent(HomeActivity.this, DownloadActivity.class));
                                } else if (item.getItemId() == 10003) {
                                    startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
                                }
                            }
                        });

                    } else if (item.getItemId() == 10004) {
                        shareApp();
                    }

                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Constant.showRateDialog(this, true);
    }


    public void showInfoDialog() {
        try {
            Dialog mDialog = new Dialog(this, R.style.NormalDialog);
            DialogInfoBinding binding = DialogInfoBinding.inflate(getLayoutInflater());
            mDialog.setContentView(binding.getRoot());
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDialog.show();

            binding.txtError.setText(PowerPreference.getDefaultFile().getString(Constant.mNotice, getResources().getString(R.string.info)));

            binding.txtRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });

        } catch (Exception e) {
            Log.w("Catch", Objects.requireNonNull(e.getMessage()));
        }
    }


    public void setTabs() {
        List<Category> categories = viewModel.getAllCategory();
        Menu menu = binding.navView.getMenu();
        int position = 0;

        pagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        for (int i = 0; i < categories.size(); i++) {
            position = i;
            strings[i] = categories.get(i).getName();
            menu.add(0, Integer.parseInt(categories.get(i).getId()), i, categories.get(i).getName()).setCheckable(true);
            switch (categories.get(i).getType()) {
                case 0:
                    pagerFragmentAdapter.addFragment(ChannelFragment.newInstance(HomeActivity.this, categories.get(i).getId()), categories.get(i).getName());
                    break;
                case 1:
                    pagerFragmentAdapter.addFragment(PdfFragment.newInstance(HomeActivity.this, categories.get(i).getId()), categories.get(i).getName());
                    break;
                case 2:
                    pagerFragmentAdapter.addFragment(BannerFragment.newInstance(HomeActivity.this, categories.get(i).getId()), categories.get(i).getName());
                    break;
                default:
                    break;
            }
        }

        position++;
        menu.add(0, 10001, position, "Favorites").setCheckable(false);

        position++;
        menu.add(0, 10002, position, "Downloads").setCheckable(false);

        position++;
        menu.add(0, 10003, position, "Feedback").setCheckable(false);

        position++;
        menu.add(0, 10004, position, "Share App").setCheckable(false);

        binding.vpHome.setAdapter(pagerFragmentAdapter);
        if (pagerFragmentAdapter.getItemCount() > 0) {
            binding.vpHome.setOffscreenPageLimit(pagerFragmentAdapter.getItemCount());

            binding.includedProgress.progress.setVisibility(View.GONE);
            binding.includedProgress.llError.setVisibility(View.GONE);
        } else {
            binding.includedProgress.progress.setVisibility(View.GONE);
            binding.includedProgress.llError.setVisibility(View.VISIBLE);
        }
        new TabLayoutMediator(binding.tabHome, binding.vpHome, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(pagerFragmentAdapter.mFragmentTitleList.get(position));
            }
        }).attach();


    }


}