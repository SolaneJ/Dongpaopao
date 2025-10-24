import App from './App'
import { createSSRApp } from 'vue';
import * as Pinia from 'pinia';

import uviewPlus from '@/uni_modules/uview-plus'
import request from '@/utils/request.js'

export function createApp() {
	const app = createSSRApp(App);
	
	app.config.globalProperties.$u = uviewPlus;
	app.config.globalProperties.$request = request;
	
	app.use(Pinia.createPinia());
	app.use(uviewPlus)
	return {
		app,
		Pinia, // 此处必须将 Pinia 返回
	};
}