package cn.zipper.feishu.plugin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.zipper.feishu.plugin.utils.ImageUtils
import cn.zipper.feishu.plugin.utils.NotificationListenerUtils
import cn.zipper.feishu.plugin.utils.SPUtil
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.*


@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnNotify.setOnClickListener {
            val dialog = AlertDialog
                .Builder(this)
                .setMessage("打开通知栏权限，是为了监听飞书的红包消息")
                .setPositiveButton("我知道了") { p0, p1 ->

                    NotificationListenerUtils.checkService(this)

                    val enabled = NotificationListenerUtils.isNotificationListenersEnabled(this)
                    btnNotify.text = if (enabled) "通知栏权限（开启）" else "通知栏权限（关闭）"
                }
                .setCancelable(false)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
        btnSource.setOnClickListener {
            //https://github.com/moz1q1/FeiShuPlugin
            val uri: Uri = Uri.parse("https://github.com/moz1q1/FeiShuPlugin")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        btnKeep.setOnClickListener {
            val dialog = AlertDialog
                .Builder(this)
                .setMessage("增加飞书保活，是利用通知栏监听信息就把应用拉起来，看自己需求情况使用")
                .setPositiveButton("我知道了") { p0, p1 ->

                    val is_feishu_keepFirst =
                        SPUtil.get(this, SPUtil.FILE_NAME, "feishu_keep", false) as Boolean
                    SPUtil.put(this, SPUtil.FILE_NAME, "feishu_keep", !is_feishu_keepFirst)

                    val is_feishu_keep =
                        SPUtil.get(this, SPUtil.FILE_NAME, "feishu_keep", false) as Boolean
                    btnKeep.text = if (is_feishu_keep) "增强飞书保活（开启）" else "增强飞书保活（关闭）"
                }
                .setCancelable(false)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
        ivZfbQrCode.setOnClickListener {
            needPermissionWithPermissionCheck()
        }
    }

    override fun onResume() {
        super.onResume()

        val enabled = NotificationListenerUtils.isNotificationListenersEnabled(this)
        btnNotify.text = if (enabled) "通知栏权限（开启）" else "通知栏权限（关闭）"

        val is_feishu_keep = SPUtil.get(this, SPUtil.FILE_NAME, "feishu_keep", false) as Boolean
        btnKeep.text = if (is_feishu_keep) "增强飞书保活（开启）" else "增强飞书保活（关闭）"
    }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun needPermission() {
        Thread(Runnable {
            val decodeResource = BitmapFactory.decodeResource(resources, R.mipmap.ic_zhifubao)
            val saveImageToGallery = ImageUtils.saveImageToGallery(this, decodeResource)
            if (saveImageToGallery) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "保存图片成功", Toast.LENGTH_SHORT).show()
                }
            }
        }).start()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onShowRationale(request: PermissionRequest) {
        val dialog = AlertDialog
            .Builder(this)
            .setMessage("赋予SD卡权限")
            .setPositiveButton("我知道了") { p0, p1 -> request.proceed() }
            .setCancelable(false)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    @OnPermissionDenied(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionDenied() {
        Toast.makeText(applicationContext, "SD卡权限授权失败", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onNeverAskAgain() {
        val dialog = AlertDialog
            .Builder(this)
            .setMessage("赋予SD卡权限")
            .setPositiveButton("我知道了") { p0, p1 -> needPermissionWithPermissionCheck() }
            .setCancelable(false)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}
