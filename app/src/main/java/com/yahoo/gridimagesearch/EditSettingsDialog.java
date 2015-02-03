package com.yahoo.gridimagesearch;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sraovasu on 2/1/15.
 */
public class EditSettingsDialog extends DialogFragment {

    public interface EditSettingsDialogListener{
        void onSaveOrCancelSettings(String imageSize, String imageColor, String imageType, String siteSearch);
    }

    private ArrayList<String> imageSizes = new ArrayList<>(Arrays.asList("small", "medium","large","xlarge"));
    private ArrayList<String> imageColors = new ArrayList<>(Arrays.asList("black", "blue","brown","gray","green"));
    private ArrayList<String> imageTypes = new ArrayList<>(Arrays.asList("faces", "photo","clipart","lineart"));

    private String imageSize = null;
    private String imageColor = null;
    private String imageType = null;
    private String siteSearch = null;
    private EditText etSiteSearch = null;

    public EditSettingsDialog() {
        /*
        imgsz
        imgcolor
        imgtype
        as_sitesearch
        * */
    }

    public static EditSettingsDialog newInstance(String imageSize, String imageColor, String imageType, String siteSearch) {
        EditSettingsDialog fragment = new EditSettingsDialog();
        Bundle args = new Bundle();
        if (imageSize != null) { args.putString("imageSize", imageSize); }
        if (imageColor != null) { args.putString("imageColor", imageColor); }
        if (imageType != null) { args.putString("imageType", imageType); }
        if (siteSearch != null) { args.putString("siteSearch", siteSearch); }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_settings, container);
        getDialog().setTitle(R.string.settings_title);

        this.imageSize = getArguments().getString("imageSize", null);
        this.imageColor = getArguments().getString("imageColor", null);
        this.imageType = getArguments().getString("imageType", null);
        this.siteSearch = getArguments().getString("siteSearch", null);

        final EditSettingsDialog instance = this;

        Spinner sImageSize = (Spinner)view.findViewById(R.id.sImageSize);
        if (imageSize != null) {
            int selectItemIndex = this.imageSizes.indexOf(imageSize);
            if (this.isIndexWithinBounds(selectItemIndex,imageSizes)) {
                sImageSize.setSelection(selectItemIndex);
            }
        }
        sImageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (instance.imageSize == null && position == 0) {
                    return;
                }
                instance.imageSize = instance.imageSizes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sColorFilter = (Spinner)view.findViewById(R.id.sColorFilter);
        if (imageColor != null) {
            int selectItemIndex = this.imageColors.indexOf(imageColor);
            if (this.isIndexWithinBounds(selectItemIndex,imageColors)) {
                sColorFilter.setSelection(selectItemIndex);
            }
        }
        sColorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (instance.imageColor == null && position == 0) {
                    return;
                }
                instance.imageColor = instance.imageColors.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sImageType = (Spinner)view.findViewById(R.id.sImageType);
        if (imageType != null) {
            int selectItemIndex = this.imageTypes.indexOf(imageType);
            if (this.isIndexWithinBounds(selectItemIndex,imageTypes)) {
                sImageType.setSelection(selectItemIndex);
            }
        }
        sImageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (instance.imageType == null && position == 0) {
                    return;
                }
                instance.imageType = instance.imageTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.etSiteSearch = (EditText)view.findViewById(R.id.etSiteFilter);
        this.etSiteSearch.setText(this.siteSearch);

        Button btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(v);
            }
        });

        Button btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(v);
            }
        });

        return view;
    }

    public void onSave(View v) {
        EditSettingsDialogListener listener = (EditSettingsDialogListener) getActivity();
        String siteSearch = this.etSiteSearch.getText().toString();
        if (siteSearch.length() == 0){
            siteSearch = null;
        }
        listener.onSaveOrCancelSettings(this.imageSize,
                this.imageColor,
                this.imageType,
                siteSearch);
        dismiss();
    }

    public void onCancel(View v) {
        dismiss();
    }

    private boolean isIndexWithinBounds(int index, ArrayList<String> arrayList) {
        if (arrayList.size() == 0) {
            return false;
        }

        if (index >= 0 && index < arrayList.size()) {
            return true;
        }

        return false;
    }
}
