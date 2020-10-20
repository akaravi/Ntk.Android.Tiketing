package ntk.android.ticketing.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ntk.android.ticketing.R;
import ntk.android.ticketing.TicketingApp;
import ntk.android.ticketing.adapter.AdInbox;
import ntk.android.ticketing.event.notificationEvent;
import ntk.android.ticketing.model.NotificationModel;
import ntk.android.ticketing.room.RoomDb;

public class NotificationsActivity extends AppCompatActivity {

    @BindView(R.id.recyclerInbox)
    RecyclerView Rv;

    private ArrayList<NotificationModel> notifies = new ArrayList<>();
    private AdInbox adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inbox);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Rv.setLayoutManager(manager);
        notifies.addAll(RoomDb.getRoomDb(this).NotificationDoa().All());
        if (notifies.size() == 0) {
            Toast.makeText(this, "پیامی برای نمایش وجود ندارد", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            adapter = new AdInbox(this, notifies);
            Rv.post(() -> {
                Rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TicketingApp.Inbox = true;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TicketingApp.Inbox = false;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void SetDataChange(notificationEvent event) {
        if (event.DataChange()) {
            notifies.clear();
            notifies.addAll(RoomDb.getRoomDb(NotificationsActivity.this).NotificationDoa().All());
            Rv.post(() -> adapter.notifyDataSetChanged());
        }
    }

    @OnClick(R.id.imgBackActInbox)
    public void ClickBack() {
        finish();
    }
}