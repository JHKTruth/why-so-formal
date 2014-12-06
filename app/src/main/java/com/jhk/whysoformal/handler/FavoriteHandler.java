/*
 * Copyright 2014 Ji Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jhk.whysoformal.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class FavoriteHandler extends Handler {
    
    public static final int FAVORITE = 0; 
    
    public FavoriteHandler(Looper looper) {
        super(looper);
    }
    
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        
        if(msg.what == FAVORITE) {

        }
    }
    
}
