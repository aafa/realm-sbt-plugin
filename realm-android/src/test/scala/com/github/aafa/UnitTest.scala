package com.github.aafa

import com.github.aafa.R
import android.os.Build.VERSION_CODES.LOLLIPOP
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.scalatest.{FlatSpec, Matchers, RobolectricSuite}

@Config(sdk = Array(LOLLIPOP))
class UnitTest extends FlatSpec with Matchers with RobolectricSuite {
  "Resources" should "be accessible via R" in {
    RuntimeEnvironment.application.getString(R.string.app_name) shouldBe "realm-test"
  }

  "Package name" should "as expected" in {
    RuntimeEnvironment.application.getBaseContext.getPackageName shouldBe "com.github.aafa"
  }

}