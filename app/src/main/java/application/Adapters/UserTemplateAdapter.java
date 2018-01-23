package application.Adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import application.Users.UserTemplate;
import atchat.aegis.com.myapplication.R;

/**
 * Created by Avi on 2018-01-22.
 */

public class UserTemplateAdapter extends ArrayAdapter<UserTemplate>{
    private final Context context;
    private final UserTemplate[] userTemplates;

    public UserTemplateAdapter(@NonNull Context context,  @NonNull UserTemplate[] objects) {
        super(context, R.layout.contact_layout, objects);
        this.context = context;
        userTemplates = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contact_layout, parent, false);
        TextView friendsName = (TextView) rowView.findViewById(R.id.friendNames);
        ImageView friendImage = (ImageView) rowView.findViewById(R.id.friendProfilePic);
        friendsName.setText(userTemplates[position].getName());
        return rowView;
    }
}
