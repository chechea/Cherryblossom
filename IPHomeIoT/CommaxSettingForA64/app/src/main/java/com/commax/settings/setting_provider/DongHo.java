package com.commax.settings.setting_provider;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class DongHo {

    private static final String ACTION = "commax.action.updated.RESIDENCE_NUMBER";
    private static final String MYSQL_GET = "CALL db_house.proc_dongho_info()";
    private static final String MYSQL_SET = "CALL db_house.proc_dong_ho_update(\"";
    private static final int PROVIDER_INDEX = 9;

    private boolean isMysql;
    private Context context;

    public DongHo(Context context) {
        this.context = context;

        isMysql = new ValueType().getValue();

    }

//	public EntryPair getValuePair() throws FileNotFoundException, IOException {
//
//		String value = getValue();
//		String[] arr = value.split(Symbol.SYMBOL_DASH);
//		if (arr.length != 2) {
//			return null;
//		}
//
//		return new EntryPair(arr[0], arr[1]);
//	}

    public String getValueProvider() {

        return new SettingProvider(context).getValue(PROVIDER_INDEX);
    }

    public String getValue() {
        String dong = null;
        String ho = null;

        String ret = null;
        String provider = new SettingProvider(context).getValue(PROVIDER_INDEX);


        if (provider != null) {
            if (provider.contains(Symbol.SYMBOL_DASH)) {

                String[] arr = provider.split(Symbol.SYMBOL_DASH);
                if (arr.length == 2) {
                    dong = arr[0];
                    ho = arr[1];

//					if (isMysql) {
//						// mysql
//						String sql = new SoapHelper(context).getValue(MYSQL_GET);
//						if (sql != null) {
//							if (sql.contains("&amp;")) {
//								String[] array = sql.split("&amp;");
//								if (array.length >= 2) {
//									String first = array[0];
//									String second = array[1];
//									if (dong.equals(first) && ho.equals(second)) {
//										ret = dong + "-" + ho;
//									}
//								}
//
//							} else if (sql.contains(Symbol.SYMBOL_AMPERSAND)) {
//
//								String[] array = sql
//										.split(Symbol.SYMBOL_AMPERSAND);
//								if (array.length >= 3) {
//									String first = array[1];
//									String second = array[2];
//
//									if (dong.equals(first) && ho.equals(second)) {
//										ret = dong + "-" + ho;
//									}
//								}
//
//							}
//						}
//
//					}
//					 else {
//						// provider
//						ret = dong + "-" + ho;
//					}


                    // provider
                    ret = dong + "-" + ho;
                }

            }

        }
        return ret;
    }

    public void getValueInBackground(final OnStringCallback l) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {

                return getValue();
            }

            @Override
            protected void onPostExecute(String result) {
                if (l != null) {
                    l.onResult(result);
                }
            }
        }.execute();
    }

    public boolean setValue(String value) {
        if (value == null) {
            return false;
        }
        if (!value.contains("-")) {
            return false;
        }
        String[] array = value.split("-");
        String dong = array[0];
        String ho = array[1];

        return setValue(dong, ho);
    }

    public boolean setValue(String dong, String ho) {
//		if (isMysql) {
//			StringBuffer buf = new StringBuffer();
//			buf.append(MYSQL_SET);
//			buf.append(dong);
//			buf.append("\",\"");
//			buf.append(ho);
//			buf.append("\")");
//
//			boolean condition = new SoapHelper(context)
//					.setValue(buf.toString());
//			if (!condition) {
//				return false;
//			}
//		}

        // provider
        StringBuffer buf = new StringBuffer();
        buf.append(dong);
        buf.append("-");
        buf.append(ho);

        boolean ret = new SettingProvider(context).setValue(PROVIDER_INDEX,
                buf.toString());
        if (ret) {
            sendBroadcast(ACTION);
        }

        return ret;

    }

    public void setValueInBackground(final String dong, final String ho,
                                     final OnBooleanCallback l) {
        new AsyncTask<Void, Void, Void>() {
            boolean value;

            @Override
            protected Void doInBackground(Void... params) {
                value = setValue(dong, ho);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (l != null) {
                    l.onResult(value);
                }
            }

        }.execute();
    }

    public void sendBroadcast(String action) {

        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

}
