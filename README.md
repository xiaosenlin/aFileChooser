com.ipaulpro.afilechooser包下面是原生的

com.ssa.afilechooser是我稍加修改的
使用方法如下：
# 1.首先在AndroidManifest.xml中添加：
	<activity
            android:name="com.ecity.afilechooser.FileChooserActivity2"
            android:exported="true"
            android:icon="@drawable/ic_chooser"
            android:label="@string/choose_file" >
            <intent-filter>
                <action android:name="android.intent.action.GET_CONTENT" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.OPENABLE" />
                <data android:mimeType="*/*" />
            </intent-filter>
        </activity>

# 2.在要使用的mActivity中添加代码
FileUtils2.mFileFileterBySuffixs.acceptSuffixs("amr|mp3");//过江哪些格式的文件，用“|”分隔（英文），如果不加这句代码，默认显示所有文件。
Intent intent = new Intent(this, FileChooserActivity2.class);
mActivity.startActivityForResult(intent, 1);
# 3.在使用的mActivity中添加方法：
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (null != data) {
                    @SuppressWarnings("unchecked")
                    ArrayList<File> files = (ArrayList<File>) data.getSerializableExtra(FileChooserActivity2.PATHS);//返回的一个ArrayList<File>     
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


# 其它说明：
## 1.FileChooserActivity2暂为默认"文件选择" 未提供更改标题的接口，如有需要，可自行修改
## 2.如有问题，可以到CSDN留言(http://blog.csdn.net/sunshanai/article/details/51532922) ，一起讨论，谢谢。
