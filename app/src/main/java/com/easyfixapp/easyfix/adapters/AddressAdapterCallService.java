package com.easyfixapp.easyfix.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyfixapp.easyfix.R;
import com.easyfixapp.easyfix.activities.MapActivity;
import com.easyfixapp.easyfix.activities.MapCallService;
import com.easyfixapp.easyfix.models.Address;
import com.easyfixapp.easyfix.util.ApiService;
import com.easyfixapp.easyfix.util.ServiceGenerator;
import com.easyfixapp.easyfix.util.SessionManager;
import com.easyfixapp.easyfix.util.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class AddressAdapterCallService extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private List<Address> mAddressList;
    private Context mContext;
    private int defaultIdAddress = -1;
    private int defaultPositionAddress = -1;

    public AddressAdapterCallService(Context context, List<Address> addressList) {
        this.mAddressList = addressList;
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_address, viewGroup, false);

        if (viewType == TYPE_ITEM) {
            return new AddressViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            ((AddressViewHolder) holder).bind(position);
        }else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return mAddressList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }


    /**
     * Custom validation
     */

    private boolean isPositionFooter(int position) {
        int size = mAddressList.size();
        return size == 0 || position == size;
    }


    private void removeItem(int position) {

        if (mAddressList.get(position).isDefault()) {
            defaultIdAddress = -1;
            defaultPositionAddress = -1;
        }

        mAddressList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mAddressList.size());
    }

    private void updateItems(int position1, Address address1, int position2, Address address2) {
        mAddressList.set(position1, address1);
        mAddressList.set(position2, address2);

        notifyItemChanged(position1);
        notifyItemChanged(position2);
    }

    private void updateItem(int position, Address address) {
        mAddressList.set(position, address);
        notifyItemChanged(position);
    }

    /**
     * View Holders
     */

    public class AddressViewHolder
            extends RecyclerView.ViewHolder{

        public CircleImageView mStatusView;
        public TextView mNameView, mDescriptionView;
        public ImageView mActionView;
        public LinearLayout mItemList;

        public AddressViewHolder(View itemView) {
            super(itemView);
            mStatusView = itemView.findViewById(R.id.img_status);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
            mActionView = itemView.findViewById(R.id.img_action);
            mActionView.setVisibility(GONE);
            mItemList = itemView.findViewById(R.id.list_item_direction);
        }

        public void bind(final int position) {

            final Address address = mAddressList.get(position);


            if (address.isDefault()) {
                defaultIdAddress = address.getId();
                defaultPositionAddress = position;
                //mStatusView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add));
//                mStatusView.setOnClickListener(null);
                mStatusView.setCircleBackgroundColorResource(R.color.green);
            } else {
                //mStatusView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.ic_dialog_dialer));
                mStatusView.setCircleBackgroundColor(Color.GRAY);



                mStatusView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                        builder.setMessage(R.string.address_message_update_dialog)
                                .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        updateAddressTask(address.getId(), position);
                                    }
                                })
                                .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                });

                        builder.show();
                    }
                });
            }



            mStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_update_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    updateAddressTask(address.getId(), position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                }
            });

            mNameView.setText(address.getName());
            mDescriptionView.setText(address.getDescription());
            mActionView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_delete));

            mActionView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
                    builder.setMessage(R.string.address_message_delete_dialog)
                            .setPositiveButton(R.string.dialog_message_yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteAddressTask(address.getId(), position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_message_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });

                    builder.show();
                }
            });


//TODO OnclickListener para tomar las direcciones si no lo logro hacer desde la activity
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder displayAlert = new AlertDialog.Builder(mContext, R.style.AlertDialog);
//
//                    displayAlert.setCancelable(false);
//                    displayAlert.setMessage("¿Cuánto puedes esperar?")
//                            .setPositiveButton(R.string.dialog_1_hour, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //TODO agendar una hora respecto al tiempo actual
////                            Address address = new Address();
////                            address.setName(mName);
////                            address.setDescription(mDescription);
////                            address.setReference(mReferenceView.getText().toString());
////                            address.setLatitude(String.valueOf(mLastLocation.latitude));
////                            address.setLongitude(String.valueOf(mLastLocation.longitude));
////
////                            address.setActive(true);
////                            address.setDefault(checkBox.isChecked());
////
////                            if (mAddress == null) {
////                                createAddressTask(address);
////                            } else {
////                                address.setId(mAddress.getId());
////                                updateAddressTask(address);
////                            }
//                                }
//                            })
//                            .setNegativeButton(R.string.dialog_2_hours, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    //TODO agendar dos horas respecto al tiempo actual
//                                }
//                            })
//                            .setNeutralButton(R.string.action_service_detail_schedule, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    //TODO Abrir el fragment de agendar
//                                }
//                            }).setCancelable(true);
//
//                    displayAlert.show();
//
////                    Bundle bundle = new Bundle();
////                    bundle.putSerializable("address", address);
////
////                    Intent intent = new Intent(mContext, MapActivity.class);
////                    intent.putExtras(bundle);
////                    mContext.startActivity(intent);
//
//                }
//            });
        }

    }

    public class FooterViewHolder
            extends RecyclerView.ViewHolder {

        public CircleImageView mStatusView;
        public TextView mNameView, mDescriptionView;
        public ImageView mActionView;
        public LinearLayout mItemList;


        public FooterViewHolder(View itemView) {
            super(itemView);
            mStatusView = itemView.findViewById(R.id.img_status);
            mNameView = itemView.findViewById(R.id.txt_name);
            mDescriptionView = itemView.findViewById(R.id.txt_address);
            mActionView = itemView.findViewById(R.id.img_action);
            mItemList = itemView.findViewById(R.id.list_item_direction);
        }

        public void bind() {

            mDescriptionView.setVisibility(GONE);

//            mNameView.setText(itemView.getContext().getString(R.string.address_message_footer));
//            mItemList.setVisibility(GONE);
//            mActionView.setImageDrawable(mContext
//                    .getResources().getDrawable(R.drawable.ic_action_right));
//            //mActionView.getLayoutParams().height = 65;
//            //mActionView.getLayoutParams().width = 65;
//
//            mActionView.setOnClickListener(new View.OnClickListener() {
//                @Override public void onClick(View v) {
//                    Intent intent = new Intent(mContext, MapActivity.class);
//                    mContext.startActivity(intent);
//                }
//            });
//
//
//            mStatusView.setCircleBackgroundColor(Color.GRAY);
        }

    }


    /**
     * Task
     */
    private void deleteAddressTask(final int pk, final int position){
        Util.showLoading(mContext, mContext.getString(R.string.address_message_delete_request));

        SessionManager sessionManager = new SessionManager(mContext);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Void> call = apiService.deleteAddress(pk, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(mContext,
                            mContext.getString(R.string.address_message_delete_response));
                    // Update
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        Address a = realm
                                .where(Address.class)
                                .equalTo("id", pk)
                                .findFirst();

                        if (a != null) {
                            realm.beginTransaction();
                            a.setActive(false);
                            realm.copyToRealmOrUpdate(a);
                            realm.commitTransaction();
                        }
                        removeItem(position);
                    } finally {
                        realm.close();
                    }
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(mContext,
                            mContext.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(mContext,
                        mContext.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }

    private void updateAddressTask(final int pk, final int position){
        Util.showLoading(mContext, mContext.getString(R.string.address_message_update_request));

        SessionManager sessionManager = new SessionManager(mContext);
        String token = sessionManager.getToken();

        ApiService apiService = ServiceGenerator.createApiService();
        Call<Void> call = apiService.updateAddress(pk, token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(Util.TAG_ADDRESS, "Address result: success!");
                    Util.longToast(mContext,
                            mContext.getString(R.string.address_message_update_response));

                    // Update
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        Address addressBefore = realm
                                .where(Address.class)
                                .equalTo("id", defaultIdAddress)
                                .findFirst();

                        Address addressAfter = realm
                                .where(Address.class)
                                .equalTo("id", pk)
                                .findFirst();

                        realm.beginTransaction();

                        addressAfter.setDefault(true);
                        realm.copyToRealmOrUpdate(addressAfter);

                        realm.commitTransaction();

                        int position2 = position;
                        Address address2 = realm.copyFromRealm(addressAfter);

                        if (addressBefore != null) {

                            realm.beginTransaction();

                            addressBefore.setDefault(false);
                            realm.copyToRealmOrUpdate(addressBefore);

                            realm.commitTransaction();

                            // Notify data
                            int position1 = defaultPositionAddress;
                            Address address1 = realm.copyFromRealm(addressBefore);

                            updateItems(position1, address1, position2, address2);
                        } else {
                            updateItem(position2, address2);
                        }


                    } catch (Exception e){
                        e.getStackTrace();
                        Log.i(Util.TAG_ADDRESS, e.getMessage());
                    } finally {
                        realm.close();
                    }
                } else {
                    Log.i(Util.TAG_ADDRESS, "Address result: " + response.toString());
                    Util.longToast(mContext,
                            mContext.getString(R.string.message_service_server_failed));
                }
                Util.hideLoading();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(Util.TAG_ADDRESS, "Address result: failed, " + t.getMessage());
                Util.longToast(mContext,
                        mContext.getString(R.string.message_network_local_failed));
                Util.hideLoading();
            }
        });
    }
}
