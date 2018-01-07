package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.httptandooripalace.restaurantorderprinter.R;
import entities.Bill;
import helpers.Rounder;

/**
 * Created by uiz on 27/04/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<Bill> bills;

    public HistoryAdapter(Context c, List<Bill> bills) {
        context = c;
        this.bills = new ArrayList<>();
        this.bills = bills;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.tvId.setText(bill.getId() + "");
        holder.tvDate.setText(bill.getDate().toString().substring(4, 10));
        holder.tvTable.setText(bill.getTableNr() + "");
        holder.tvWaiter.setText(bill.getWaiter() + "");
        holder.tvPrice.setText(Rounder.round(bill.getTotal_price_excl()) + " €");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvId;
        public TextView tvDate;
        public TextView tvTable;
        public TextView tvWaiter;
        public TextView tvPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.date);
            tvId = (TextView) itemView.findViewById(R.id.id);
            tvTable = (TextView) itemView.findViewById(R.id.table);
            tvWaiter = (TextView) itemView.findViewById(R.id.waiter);
            tvPrice = (TextView) itemView.findViewById(R.id.price);
        }
    }


}
