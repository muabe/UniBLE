package com.muabe.ble.sample

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.markjmind.uni.UniIntroFragment
import com.markjmind.uni.mapper.annotiation.Binder
import com.markjmind.uni.mapper.annotiation.Timeout
import com.markjmind.uni.thread.CancelAdapter
import com.markjmind.uni.thread.LoadEvent
import com.muabe.ble.sample.databinding.IntroFragmentBinding
import com.muabe.uniboot.util.AutoPermission
import java.util.ArrayList

@Timeout(1000)
class IntroFragment : UniIntroFragment() {
    @Binder
    lateinit var binder: IntroFragmentBinding

    override fun onPre() {

    }

    override fun onLoad(event: LoadEvent?, cancelAdapter: CancelAdapter?) {
        event?.lockedUpdate(null)
    }

    override fun onUpdate(value: Any?, cancelAdapter: CancelAdapter?) {
        val autoPermission = AutoPermission()
        val array = arrayOfNulls<String?>(autoPermission.getDangerous(context).size)
        autoPermission.getDangerous(context).toArray(array)
        if (array.size > 0) {
            val permissionlistener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    log.i("PermissionCheck 끝")
                    cancelAdapter?.unlock()
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String?>?) {
                    activity?.finish()
                }

            }
            TedPermission(context)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(
                    """
                        접근항목을 확인하고 동의해 주세요.
                        동의하지 않을 경우, 앱 서비스 이용에
                        제한을 받을 수 있습니다.
                        """.trimIndent()
                )
                .setDeniedMessage("필수권한에 동의하지 않으면 서비스를 이용할수 없습니다.\n\n권한 설정을 해주세요\n[Setting] > [Permission]")
                .setPermissions(*array)
                .check()
        } else {
            cancelAdapter?.unlock()
        }
    }

    override fun onPost() {
        builder.setHistory(false).replace(MainFragment())
    }
}