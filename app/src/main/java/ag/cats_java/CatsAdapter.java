package ag.cats_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ag.cats_java.model.CatsResponse;

public class CatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CatsResponse> list = new ArrayList<>();
    private static final int TYPE_FULL = 0;
    private static final int TYPE_HALF = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final int type = viewType;
                final ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams sglp =
                            (StaggeredGridLayoutManager.LayoutParams) lp;
                    switch (type) {
                        case TYPE_FULL:
                            sglp.setFullSpan(true);
                            break;
                        case TYPE_HALF:
                            sglp.setFullSpan(false);
                            break;
                    }
                    itemView.setLayoutParams(sglp);
                    final StaggeredGridLayoutManager lm =
                            (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
                    lm.invalidateSpanAssignments();
                }
                itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        BigCardViewHolder holder = new BigCardViewHolder(itemView);
        return holder;
    }

    int selectedRBPosition = -1;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CatsResponse cat = list.get(position);

        Picasso.get().load(cat.getImageUrl()).fit().into(((BigCardViewHolder) holder).catIV);

            ((BigCardViewHolder) holder).catNameTV.setText(cat.getName());
            ((BigCardViewHolder) holder).catAgeTV.setText(cat.getAge());

            ((BigCardViewHolder) holder).radio.setChecked(selectedRBPosition == position);
            ((BigCardViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedRBPosition = ((BigCardViewHolder) holder).getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
            cat.setVisible(true);

    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType().equals("BIG_CARD"))
            return TYPE_FULL;
        else
            return TYPE_HALF;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    void addList(ArrayList<CatsResponse> items){
        list.addAll(items);
        notifyDataSetChanged();
    }


    void clear(){
        list.clear();
        notifyDataSetChanged();
    }


    class BigCardViewHolder extends RecyclerView.ViewHolder {

        TextView catNameTV, catAgeTV;
        ImageView catIV;
        RadioButton radio;

        public BigCardViewHolder(@NonNull View itemView) {
            super(itemView);

            catNameTV = itemView.findViewById(R.id.catNameTV);
            catAgeTV = itemView.findViewById(R.id.catAgeTV);
            catIV = itemView.findViewById(R.id.catIV);
            radio = itemView.findViewById(R.id.radio);
        }

    }

}
