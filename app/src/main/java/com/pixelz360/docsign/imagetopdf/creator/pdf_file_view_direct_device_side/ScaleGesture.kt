package com.app.pdfconverter.compresspdf.mergepdffiles.fillandsign.pdfsigner.docusign.touchlistner

import android.util.Log
import android.view.MotionEvent
import android.view.View

class ScaleGesture(private val mListener: OnScaleGestureListener) {
    private var mActive0MostRecent = false
    private var mActiveId0 = 0
    private var mActiveId1 = 0
    private var mCurrEvent: MotionEvent? = null
    var currentSpanX = 0f
        private set
    var currentSpanY = 0f
        private set
    private var mCurrLen = 0f
    private var mCurrPressure = 0f
    val currentSpanVector = Vector2D()
    var focusX = 0f
        private set
    var focusY = 0f
        private set
    var isInProgress = false
        private set
    private var mInvalidGesture = false
    private var mPrevEvent: MotionEvent? = null
    var previousSpanX = 0f
        private set
    var previousSpanY = 0f
        private set
    private var mPrevLen = 0f
    private var mPrevPressure = 0f
    private var mScaleFactor = 0f
    var timeDelta: Long = 0
        private set

    interface OnScaleGestureListener {
        fun onScale(view: View?, scaleGesture: ScaleGesture?): Boolean
        fun onScaleBegin(view: View?, scaleGesture: ScaleGesture?): Boolean
        fun onScaleEnd(view: View?, scaleGesture: ScaleGesture?)
    }

    open class SimpleOnScaleGestureListener : OnScaleGestureListener {
        override fun onScale(view: View?, scaleGesture: ScaleGesture?): Boolean {
            return false
        }

        override fun onScaleBegin(
            view: View?,
            scaleGesture: ScaleGesture?
        ): Boolean {
            return true
        }

        override fun onScaleEnd(view: View?, scaleGesture: ScaleGesture?) {}
    }

    fun onTouchEvent(view: View, motionEvent: MotionEvent): Boolean {
        val i: Int
        val actionMasked = motionEvent.actionMasked
        if (actionMasked == 0) {
            reset()
        }
        var z = false
        if (mInvalidGesture) {
            return false
        }
        return if (isInProgress) {
            when (actionMasked) {
                1 -> {
                    reset()
                    true
                }
                2 -> {
                    setContext(view, motionEvent)
                    if (mCurrPressure / mPrevPressure <= PRESSURE_THRESHOLD || !mListener.onScale(
                            view,
                            this
                        )
                    ) {
                        return true
                    }
                    mPrevEvent!!.recycle()
                    mPrevEvent = MotionEvent.obtain(motionEvent)
                    true
                }
                3 -> {
                    mListener.onScaleEnd(view, this)
                    reset()
                    true
                }
                5 -> {
                    mListener.onScaleEnd(view, this)
                    var i2 = mActiveId0
                    val i3 = mActiveId1
                    reset()
                    mPrevEvent = MotionEvent.obtain(motionEvent)
                    if (!mActive0MostRecent) {
                        i2 = i3
                    }
                    mActiveId0 = i2
                    mActiveId1 = motionEvent.getPointerId(motionEvent.actionIndex)
                    mActive0MostRecent = false
                    if (motionEvent.findPointerIndex(mActiveId0) < 0 || mActiveId0 == mActiveId1) {
                        mActiveId0 =
                            motionEvent.getPointerId(
                                findNewActiveIndex(
                                    motionEvent,
                                    mActiveId1,
                                    -1
                                )
                            )
                    }
                    setContext(view, motionEvent)
                    isInProgress = mListener.onScaleBegin(view, this)
                    true
                }
                6 -> {
                    val pointerCount = motionEvent.pointerCount
                    val actionIndex = motionEvent.actionIndex
                    val pointerId = motionEvent.getPointerId(actionIndex)
                    if (pointerCount > 2) {
                        if (pointerId == mActiveId0) {
                            val findNewActiveIndex =
                                findNewActiveIndex(motionEvent, mActiveId1, actionIndex)
                            if (findNewActiveIndex >= 0) {
                                mListener.onScaleEnd(view, this)
                                mActiveId0 = motionEvent.getPointerId(findNewActiveIndex)
                                mActive0MostRecent = true
                                mPrevEvent = MotionEvent.obtain(motionEvent)
                                setContext(view, motionEvent)
                                isInProgress = mListener.onScaleBegin(view, this)
                                mPrevEvent!!.recycle()
                                mPrevEvent = MotionEvent.obtain(motionEvent)
                                setContext(view, motionEvent)
                            }
                        } else {
                            if (pointerId == mActiveId1) {
                                val findNewActiveIndex2 =
                                    findNewActiveIndex(motionEvent, mActiveId0, actionIndex)
                                if (findNewActiveIndex2 >= 0) {
                                    mListener.onScaleEnd(view, this)
                                    mActiveId1 = motionEvent.getPointerId(findNewActiveIndex2)
                                    mActive0MostRecent = false
                                    mPrevEvent = MotionEvent.obtain(motionEvent)
                                    setContext(view, motionEvent)
                                    isInProgress = mListener.onScaleBegin(view, this)
                                }
                            }
                            mPrevEvent!!.recycle()
                            mPrevEvent = MotionEvent.obtain(motionEvent)
                            setContext(view, motionEvent)
                        }
                        z = true
                        mPrevEvent!!.recycle()
                        mPrevEvent = MotionEvent.obtain(motionEvent)
                        setContext(view, motionEvent)
                    } else {
                        z = true
                    }
                    if (!z) {
                        return true
                    }
                    setContext(view, motionEvent)
                    i = if (pointerId == mActiveId0) {
                        mActiveId1
                    } else {
                        mActiveId0
                    }
                    val findPointerIndex = motionEvent.findPointerIndex(i)
                    focusX = motionEvent.getX(findPointerIndex)
                    focusY = motionEvent.getY(findPointerIndex)
                    mListener.onScaleEnd(view, this)
                    reset()
                    mActiveId0 = i
                    mActive0MostRecent = true
                    true
                }
                else -> true
            }
        } else if (actionMasked != 5) {
            when (actionMasked) {
                0 -> {
                    mActiveId0 = motionEvent.getPointerId(0)
                    mActive0MostRecent = true
                    true
                }
                1 -> {
                    reset()
                    true
                }
                else -> true
            }
        } else {
            if (mPrevEvent != null) {
                mPrevEvent!!.recycle()
            }
            mPrevEvent = MotionEvent.obtain(motionEvent)
            timeDelta = 0
            val actionIndex2 = motionEvent.actionIndex
            val findPointerIndex2 = motionEvent.findPointerIndex(mActiveId0)
            mActiveId1 = motionEvent.getPointerId(actionIndex2)
            if (findPointerIndex2 < 0 || findPointerIndex2 == actionIndex2) {
                mActiveId0 =
                    motionEvent.getPointerId(findNewActiveIndex(motionEvent, mActiveId1, -1))
            }
            mActive0MostRecent = false
            setContext(view, motionEvent)
            isInProgress = mListener.onScaleBegin(view, this)
            true
        }
    }

    private fun findNewActiveIndex(motionEvent: MotionEvent, i: Int, i2: Int): Int {
        val pointerCount = motionEvent.pointerCount
        val findPointerIndex = motionEvent.findPointerIndex(i)
        for (i3 in 0 until pointerCount) {
            if (i3 != i2 && i3 != findPointerIndex) {
                return i3
            }
        }
        return -1
    }

    private fun setContext(view: View, motionEvent: MotionEvent) {
        if (mCurrEvent != null) {
            mCurrEvent!!.recycle()
        }
        mCurrEvent = MotionEvent.obtain(motionEvent)
        mCurrLen = -1.0f
        mPrevLen = -1.0f
        mScaleFactor = -1.0f
        currentSpanVector[0.0f] = 0.0f
        val motionEvent2 = mPrevEvent
        val findPointerIndex = motionEvent2!!.findPointerIndex(mActiveId0)
        val findPointerIndex2 = motionEvent2.findPointerIndex(mActiveId1)
        val findPointerIndex3 = motionEvent.findPointerIndex(mActiveId0)
        val findPointerIndex4 = motionEvent.findPointerIndex(mActiveId1)
        if (findPointerIndex < 0 || findPointerIndex2 < 0 || findPointerIndex3 < 0 || findPointerIndex4 < 0) {
            mInvalidGesture = true
            Log.e(TAG, "Invalid MotionEvent stream detected.", Throwable())
            if (isInProgress) {
                mListener.onScaleEnd(view, this)
                return
            }
            return
        }
        val x = motionEvent2.getX(findPointerIndex)
        val y = motionEvent2.getY(findPointerIndex)
        val x2 = motionEvent2.getX(findPointerIndex2)
        val y2 = motionEvent2.getY(findPointerIndex2)
        val x3 = motionEvent.getX(findPointerIndex3)
        val y3 = motionEvent.getY(findPointerIndex3)
        val f = x2 - x
        val f2 = y2 - y
        val x4 = motionEvent.getX(findPointerIndex4) - x3
        val y4 = motionEvent.getY(findPointerIndex4) - y3
        currentSpanVector[x4] = y4
        previousSpanX = f
        previousSpanY = f2
        currentSpanX = x4
        currentSpanY = y4
        focusX = x4 * 0.5f + x3
        focusY = y4 * 0.5f + y3
        timeDelta = motionEvent.eventTime - motionEvent2.eventTime
        mCurrPressure =
            motionEvent.getPressure(findPointerIndex3) + motionEvent.getPressure(findPointerIndex4)
        mPrevPressure =
            motionEvent2.getPressure(findPointerIndex) + motionEvent2.getPressure(findPointerIndex2)
    }

    private fun reset() {
        if (mPrevEvent != null) {
            mPrevEvent!!.recycle()
            mPrevEvent = null
        }
        if (mCurrEvent != null) {
            mCurrEvent!!.recycle()
            mCurrEvent = null
        }
        isInProgress = false
        mActiveId0 = -1
        mActiveId1 = -1
        mInvalidGesture = false
    }

    val currentSpan: Float
        get() {
            if (mCurrLen == -1.0f) {
                val f = currentSpanX
                val f2 = currentSpanY
                mCurrLen = Math.sqrt((f * f + f2 * f2).toDouble()).toFloat()
            }
            return mCurrLen
        }
    val previousSpan: Float
        get() {
            if (mPrevLen == -1.0f) {
                val f = previousSpanX
                val f2 = previousSpanY
                mPrevLen = Math.sqrt((f * f + f2 * f2).toDouble()).toFloat()
            }
            return mPrevLen
        }
    val scaleFactor: Float
        get() {
            if (mScaleFactor == -1.0f) {
                mScaleFactor = currentSpan / previousSpan
            }
            return mScaleFactor
        }
    val eventTime: Long
        get() = mCurrEvent!!.eventTime

    companion object {
        private const val PRESSURE_THRESHOLD = 0.67f
        private const val TAG = "ScaleGesture"
    }
}