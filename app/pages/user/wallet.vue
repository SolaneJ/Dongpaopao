<template>
	<view class="wallet-page">
		<!-- 顶部余额卡片 -->
		<view class="balance-card">
			<view class="card-content">
				<view class="balance-info">
					<text class="balance-label">余额</text>
					<text class="balance-amount">¥{{ formatAmount(totalBalance) }}</text>
				</view>
				<view class="card-decoration">
					<view class="decoration-circle circle-1"></view>
					<view class="decoration-circle circle-2"></view>
				</view>
			</view>
		</view>

		<!-- 快捷操作 -->
		<view class="quick-actions">
			<u-row gutter="20">
				<u-col span="6" v-for="(action, index) in actions" :key="index">
					<view class="action-card" @click="handleAction(action.type)">
						<view class="action-icon" :class="action.type">
							<text class="icon-text">{{ action.icon }}</text>
						</view>
						<text class="action-text">{{ action.text }}</text>
					</view>
				</u-col>
			</u-row>
		</view>

		<!-- 资金流水 -->
		<view class="records-section">
			<view class="section-header">
				<text class="section-title">资金流水</text>
				<view class="filter-tabs">
					<u-tabs :list="tabList" :current="currentTab" @change="onTabChange" :activeStyle="{
              color: '#07c160',
              fontWeight: 'bold'
            }" lineColor="#07c160"></u-tabs>
				</view>
			</view>

			<view class="records-list">
				<view v-for="record in filteredRecords" :key="record.id" class="record-item">
					<view class="record-left">
						<view class="record-icon" :class="getRecordTypeClass(record.type)">
							{{ getRecordIcon(record.type) }}
						</view>
						<view class="record-info">
							<text class="record-title">{{ record.type }}</text>
							<text class="record-desc">{{ record.desc }}</text>
							<text class="record-time">{{ record.time }}</text>
						</view>
					</view>
					<view class="record-amount" :class="getAmountClass(record.amount)">
						{{ record.amount > 0 ? '+' : '' }}{{ formatAmount(record.amount) }}
					</view>
				</view>

				<u-empty v-if="filteredRecords.length === 0" mode="list" text="暂无相关记录" marginTop="100">
				</u-empty>
			</view>
		</view>

		<!-- 充值弹窗 -->
		<u-popup :show="showRecharge" mode="bottom" round="10" closeable @close="hideRechargeDialog">
			<view class="popup-content">
				<view class="popup-header">
					<text class="popup-title">账户充值</text>
				</view>

				<view class="popup-body">
					<view class="amount-input-section">
						<text class="input-label">充值金额</text>
						<view class="amount-input-wrapper">
							<text class="currency-symbol">¥</text>
							<u-input v-model="rechargeAmount" placeholder="0.00" type="number" border="none"
								customStyle="padding: 20rpx; font-size: 36rpx;" @input="validateRechargeAmount" />
						</view>
						<text class="error-message" v-if="rechargeError">{{ rechargeError }}</text>
					</view>

					<view class="quick-amounts">
						<text class="quick-amount-label">快捷金额</text>
						<u-row gutter="10">
							<u-col span="4" v-for="amount in quickAmounts" :key="amount">
								<u-button :text="`¥${amount}`" :plain="rechargeAmount !== amount.toString()"
									:color="rechargeAmount === amount.toString() ? '#07c160' : '#718096'" size="mini"
									customStyle="margin: 5rpx; height: 70rpx;"
									@click="setRechargeAmount(amount)"></u-button>
							</u-col>
						</u-row>
					</view>

					<view class="payment-methods">
						<text class="payment-label">支付方式</text>
						<view class="wechat-payment">
							<view class="wechat-icon">💚</view>
							<view class="wechat-info">
								<text class="wechat-name">微信支付</text>
								<text class="wechat-desc">推荐使用微信支付</text>
							</view>
							<u-icon name="checkmark" color="#07c160" size="20"></u-icon>
						</view>
					</view>
				</view>

				<view class="popup-footer">
					<u-button text="立即充值" :disabled="!rechargeAmount || rechargeError" color="#07c160"
						customStyle="margin: 20rpx 0;" @click="confirmRecharge"></u-button>
				</view>
			</view>
		</u-popup>

		<!-- 提现弹窗 -->
		<u-popup :show="showWithdraw" mode="bottom" round="10" closeable @close="hideWithdrawDialog">
			<view class="popup-content">
				<view class="popup-header">
					<text class="popup-title">资金提现</text>
				</view>

				<view class="popup-body">
					<u-notice-bar :text="`可提现金额：¥${formatAmount(totalBalance)}`" bgColor="#f0f9ff" color="#07c160"
						icon="info-circle"></u-notice-bar>

					<view class="amount-input-section">
						<text class="input-label">提现金额</text>
						<view class="amount-input-wrapper">
							<text class="currency-symbol">¥</text>
							<u-input v-model="withdrawAmount" placeholder="0.00" type="number" border="none"
								customStyle="padding: 20rpx; font-size: 36rpx;" @input="validateWithdrawAmount" />
						</view>
						<text class="error-message" v-if="withdrawError">{{ withdrawError }}</text>
					</view>

					<view class="withdraw-to">
						<text class="withdraw-label">提现到</text>
						<view class="wechat-account">
							<view class="wechat-icon">💚</view>
							<view class="wechat-details">
								<text class="wechat-name">微信零钱</text>
								<text class="wechat-id">微信号：{{ wechatInfo.nickname }}</text>
							</view>
							<u-icon name="checkmark" color="#07c160" size="20"></u-icon>
						</view>
					</view>

					<u-alert type="info" description="• 提现金额将直接转入微信零钱\n• 预计2小时内到账\n• 单笔限额50,000元\n• 每日最多提现3次"
						:closable="false" margin="20rpx 0"></u-alert>
				</view>

				<view class="popup-footer">
					<u-button text="确认提现" :disabled="!withdrawAmount || withdrawError" color="#07c160"
						customStyle="margin: 20rpx 0;" @click="confirmWithdraw"></u-button>
				</view>
			</view>
		</u-popup>

		<u-loading-page :loading="loading" />
	</view>
</template>

<script setup lang="ts">
	import {
		ref,
		computed,
		Ref
	} from 'vue'
	import { useUserStore } from '@/stores/user'
	import request from '@/utils/request'
	import { onShow } from '@dcloudio/uni-app'

	const userInfo = useUserStore().info

	onShow(async () => {
		// 获取用户余额
		loading.value = true;
		await request({
			url: "/user/balance?uid=" + userInfo.uid
		}).then((res) => {
			console.log(res);
			totalBalance.value = res.data
		});
		loading.value = false;
	})

	// 响应式数据
	const totalBalance : Ref<number> = ref(5680.50)
	const currentTab = ref(0)
	const showRecharge = ref(false)
	const showWithdraw = ref(false)
	const loading : Ref<boolean> = ref(false)

	// 微信用户信息
	const wechatInfo = ref({
		nickname: '微信用户',
		avatar: ''
	})

	// 充值相关
	const rechargeAmount = ref('')
	const rechargeError = ref('')
	const quickAmounts = [100, 200, 500, 1000, 2000, 5000]

	// 提现相关
	const withdrawAmount = ref('')
	const withdrawError = ref('')

	// 操作按钮配置
	const actions = ref([{
		type: 'recharge',
		text: '充值',
		icon: '💳'
	},
	{
		type: 'withdraw',
		text: '提现',
		icon: '💰'
	}
	])

	// 标签页配置
	const tabList = ref([{
		name: '全部'
	},
	{
		name: '收入'
	},
	{
		name: '支出'
	}
	])

	// 交易记录数据
	const records = ref([{
		id: 1,
		type: '微信充值',
		desc: '账户充值',
		amount: 500.00,
		time: '10-15 09:30'
	},
	{
		id: 2,
		type: '餐饮消费',
		desc: '海底捞火锅',
		amount: -268.50,
		time: '10-14 18:45'
	},
	{
		id: 3,
		type: '购物消费',
		desc: '微信小程序购物',
		amount: -456.80,
		time: '10-13 15:20'
	},
	{
		id: 4,
		type: '微信提现',
		desc: '提现到微信零钱',
		amount: -1000.00,
		time: '10-12 10:15'
	},
	{
		id: 5,
		type: '转账收入',
		desc: '微信好友转账',
		amount: 200.00,
		time: '10-11 14:30'
	},
	{
		id: 6,
		type: '交通出行',
		desc: '微信乘车码',
		amount: -8.00,
		time: '10-10 08:20'
	},
	{
		id: 7,
		type: '退款收入',
		desc: '微信支付退款',
		amount: 198.00,
		time: '10-09 16:40'
	},
	{
		id: 8,
		type: '娱乐消费',
		desc: '微信小程序游戏',
		amount: -68.00,
		time: '10-08 20:15'
	}
	])

	// 格式化金额
	const formatAmount = (amount : number) => {
		return Math.abs(amount).toFixed(2)
	}

	// 获取记录类型样式
	const getRecordTypeClass = (type : string) => {
		const typeMap : Record<string, string> = {
			'微信充值': 'recharge',
			'微信提现': 'withdraw',
			'转账收入': 'transfer',
			'退款收入': 'refund',
			'餐饮消费': 'food',
			'购物消费': 'shopping',
			'交通出行': 'transport',
			'娱乐消费': 'entertainment'
		}
		return typeMap[type] || 'default'
	}

	// 获取记录图标
	const getRecordIcon = (type : string) => {
		const iconMap : Record<string, string> = {
			'微信充值': '💳',
			'微信提现': '💰',
			'转账收入': '🔄',
			'退款收入': '↩️',
			'餐饮消费': '🍽️',
			'购物消费': '🛍️',
			'交通出行': '🚗',
			'娱乐消费': '🎮'
		}
		return iconMap[type] || '💳'
	}

	// 获取金额样式
	const getAmountClass = (amount : number) => {
		return amount > 0 ? 'income' : 'expense'
	}

	// 过滤记录
	const filteredRecords = computed(() => {
		if (currentTab.value === 0) {
			return records.value
		} else if (currentTab.value === 1) {
			return records.value.filter(record => record.amount > 0)
		} else if (currentTab.value === 2) {
			return records.value.filter(record => record.amount < 0)
		}
		return records.value
	})

	// 标签页切换
	const onTabChange = (index : number) => {
		currentTab.value = index
	}

	// 操作处理
	const handleAction = (type : string) => {
		switch (type) {
			case 'recharge':
				showRechargeDialog()
				break
			case 'withdraw':
				showWithdrawDialog()
				break
		}
	}

	// 充值功能
	const showRechargeDialog = () => {
		showRecharge.value = true
		rechargeAmount.value = ''
		rechargeError.value = ''
	}

	const hideRechargeDialog = () => {
		showRecharge.value = false
	}

	const setRechargeAmount = (amount : number) => {
		rechargeAmount.value = amount.toString()
		validateRechargeAmount()
	}

	const validateRechargeAmount = () => {
		const amount = parseFloat(rechargeAmount.value)
		if (!amount || amount <= 0) {
			rechargeError.value = '请输入有效金额'
		} else if (amount < 1) {
			rechargeError.value = '充值金额不能少于1元'
		} else if (amount > 50000) {
			rechargeError.value = '单笔充值不能超过50,000元'
		} else {
			rechargeError.value = ''
		}
	}

	const confirmRecharge = () => {
		if (!rechargeAmount.value || rechargeError.value) return

		uni.showLoading({
			title: '调起微信支付...'
		})

		setTimeout(() => {
			uni.hideLoading()
			uni.showToast({
				title: `充值成功 ¥${rechargeAmount.value}`,
				icon: 'success'
			})

			// 更新余额
			const amount = parseFloat(rechargeAmount.value)
			totalBalance.value += amount

			// 添加交易记录
			records.value.unshift({
				id: Date.now(),
				type: '微信充值',
				desc: '账户充值',
				amount: amount,
				time: '刚刚'
			})

			hideRechargeDialog()
		}, 2000)
	}

	// 提现功能
	const showWithdrawDialog = () => {
		showWithdraw.value = true
		withdrawAmount.value = ''
		withdrawError.value = ''
	}

	const hideWithdrawDialog = () => {
		showWithdraw.value = false
	}

	const validateWithdrawAmount = () => {
		const amount = parseFloat(withdrawAmount.value)
		if (!amount || amount <= 0) {
			withdrawError.value = '请输入有效金额'
		} else if (amount < 1) {
			withdrawError.value = '提现金额不能少于1元'
		} else if (amount > totalBalance.value) {
			withdrawError.value = '提现金额不能超过总余额'
		} else if (amount > 50000) {
			withdrawError.value = '单笔提现不能超过50,000元'
		} else {
			withdrawError.value = ''
		}
	}

	const confirmWithdraw = () => {
		if (!withdrawAmount.value || withdrawError.value) return

		uni.showLoading({
			title: '处理中...'
		})

		setTimeout(() => {
			uni.hideLoading()
			uni.showToast({
				title: '提现申请已提交',
				icon: 'success'
			})

			// 更新余额
			const amount = parseFloat(withdrawAmount.value)
			totalBalance.value -= amount

			// 添加交易记录
			records.value.unshift({
				id: Date.now(),
				type: '微信提现',
				desc: '提现到微信零钱',
				amount: -amount,
				time: '刚刚'
			})

			hideWithdrawDialog()
		}, 1500)
	}
</script>

<style scoped>
	.wallet-page {
		min-height: 100vh;
		background: linear-gradient(180deg, #f8fafc 0%, #e2e8f0 100%);
		padding: 30rpx;
	}

	/* 余额卡片 */
	.balance-card {
		background: linear-gradient(135deg, #07c160 0%, #05a84e 100%);
		border-radius: 24rpx;
		padding: 40rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 10rpx 30rpx rgba(7, 193, 96, 0.3);
		position: relative;
		overflow: hidden;
	}

	.card-content {
		position: relative;
		z-index: 2;
	}

	.balance-label {
		display: block;
		color: rgba(255, 255, 255, 0.9);
		font-size: 28rpx;
		margin-bottom: 15rpx;
	}

	.balance-amount {
		display: block;
		color: white;
		font-size: 64rpx;
		font-weight: bold;
		margin-bottom: 30rpx;
	}

	/* 卡片装饰 */
	.card-decoration {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		pointer-events: none;
	}

	.decoration-circle {
		position: absolute;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.15);
	}

	.circle-1 {
		width: 120rpx;
		height: 120rpx;
		top: -40rpx;
		right: -40rpx;
	}

	.circle-2 {
		width: 80rpx;
		height: 80rpx;
		bottom: -20rpx;
		right: 60rpx;
	}

	/* 快捷操作 */
	.quick-actions {
		margin-bottom: 40rpx;
	}

	.action-card {
		background: white;
		border-radius: 16rpx;
		padding: 40rpx 20rpx;
		text-align: center;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
		transition: all 0.3s ease;
		margin-bottom: 20rpx;
	}

	.action-card:active {
		transform: scale(0.95);
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.action-icon {
		font-size: 56rpx;
		margin-bottom: 20rpx;
	}

	.action-text {
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}

	/* 资金流水区域 */
	.records-section {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	}

	.section-header {
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #2d3748;
		margin-bottom: 20rpx;
		display: block;
	}

	.filter-tabs {
		margin-top: 10rpx;
	}

	/* 记录列表 */
	.records-list {
		max-height: 600rpx;
		overflow-y: auto;
	}

	.record-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.record-item:last-child {
		border-bottom: none;
	}

	.record-left {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.record-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
		margin-right: 25rpx;
	}

	.record-icon.recharge {
		background: #e8f5e8;
	}

	.record-icon.withdraw {
		background: #fff0f0;
	}

	.record-icon.transfer {
		background: #f3e5f5;
	}

	.record-icon.refund {
		background: #fff3e0;
	}

	.record-icon.food {
		background: #ffebee;
	}

	.record-icon.shopping {
		background: #fce4ec;
	}

	.record-icon.transport {
		background: #e0f2f1;
	}

	.record-icon.entertainment {
		background: #fff8e1;
	}

	.record-icon.default {
		background: #f5f5f5;
	}

	.record-info {
		flex: 1;
	}

	.record-title {
		display: block;
		font-size: 30rpx;
		color: #2d3748;
		font-weight: 500;
		margin-bottom: 8rpx;
	}

	.record-desc {
		display: block;
		font-size: 24rpx;
		color: #718096;
		margin-bottom: 5rpx;
	}

	.record-time {
		display: block;
		font-size: 22rpx;
		color: #a0aec0;
	}

	.record-amount {
		font-size: 32rpx;
		font-weight: 600;
	}

	.record-amount.income {
		color: #07c160;
	}

	.record-amount.expense {
		color: #fa5151;
	}

	/* 弹窗样式 */
	.popup-content {
		padding: 40rpx;
	}

	.popup-header {
		margin-bottom: 30rpx;
		text-align: center;
	}

	.popup-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #1a1a1a;
	}

	.popup-body {
		margin-bottom: 40rpx;
	}

	.amount-input-section {
		margin-bottom: 40rpx;
	}

	.input-label {
		display: block;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.amount-input-wrapper {
		display: flex;
		align-items: center;
		border: 2rpx solid #e5e7eb;
		border-radius: 12rpx;
		background: #f9fafb;
		padding: 0 20rpx;
	}

	.currency-symbol {
		font-size: 36rpx;
		color: #374151;
		margin-right: 15rpx;
		font-weight: bold;
	}

	.error-message {
		display: block;
		font-size: 24rpx;
		color: #fa5151;
		margin-top: 10rpx;
	}

	.quick-amounts {
		margin-bottom: 40rpx;
	}

	.quick-amount-label {
		display: block;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.payment-label {
		display: block;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.wechat-payment {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border: 2rpx solid #07c160;
		border-radius: 12rpx;
		background: #f0f9f4;
	}

	.wechat-icon {
		font-size: 48rpx;
		margin-right: 20rpx;
	}

	.wechat-info {
		flex: 1;
	}

	.wechat-name {
		display: block;
		font-size: 30rpx;
		color: #07c160;
		font-weight: 500;
		margin-bottom: 5rpx;
	}

	.wechat-desc {
		display: block;
		font-size: 24rpx;
		color: #666;
	}

	.withdraw-to {
		margin-bottom: 30rpx;
	}

	.withdraw-label {
		display: block;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.wechat-account {
		display: flex;
		align-items: center;
		padding: 25rpx;
		border: 2rpx solid #e5e7eb;
		border-radius: 12rpx;
		background: #f9fafb;
	}

	.wechat-details {
		flex: 1;
		margin-left: 20rpx;
	}

	.wechat-id {
		display: block;
		font-size: 24rpx;
		color: #6b7280;
		margin-top: 5rpx;
	}

	.popup-footer {
		border-top: 1rpx solid #f0f0f0;
		padding-top: 30rpx;
	}
</style>