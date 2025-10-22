<!-- feeInfo.vue -->
<template>
	<!-- 费用信息区域 -->
	<view class="card">
		<u-title>费用明细</u-title>
		<view class="fee-item">
			<text>基础配送费</text>
			<u-input inputAlign="right" type="number" border="none" maxlength="4" v-model.number="baseFee"/>
		</view>
		<view class="total-fee">
			<text>总计</text>
			<text class="price">¥{{ totalFee }}</text>
		</view>
	</view>

	<!-- 底部操作区 -->
	<view class="footer">
		<view class="price-info">
			<text>合计: </text>
			<text class="total-price">¥{{ totalFee }}</text>
		</view>
		<button class="submit-btn" :disabled="submitting" :loading="submitting" @click="handleSubmit">立即下单</button>
	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		defineEmits,
		defineExpose
	} from 'vue'

	const emit = defineEmits(['submit'])

	// 费用数据
	const baseFee = ref(5.00)

	// 计算总费用
	const totalFee = computed(() => {
		return baseFee.value.toFixed(2)
	})

	// 获取费用数据
	const getFeeData = () => {
		return {
			baseFee: baseFee.value,
			totalFee: totalFee.value
		}
	}

	const submitting = ref(false)

	const handleSubmit = () => {
		submitting.value = true
		emit('submit')
	}

	// 暴露方法给父组件
	defineExpose({
		getFeeData
	})
</script>

<style scoped>
	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.fee-item {
		display: flex;
		justify-content: space-between;
		font-size: 14px;
		color: #666;
		margin-bottom: 20rpx;
	}

	.total-fee {
		display: flex;
		justify-content: space-between;
		font-size: 16px;
		color: #333;
		margin-top: 30rpx;
		padding-top: 20rpx;
		border-top: 1px dashed #eee;
	}

	.price {
		color: #ff4d4f;
		font-weight: 500;
	}

	.footer {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1px solid #eee;
	}

	.price-info {
		display: flex;
		align-items: center;
		font-size: 14px;
		color: #666;
	}

	.total-price {
		font-size: 18px;
		color: #ff4d4f;
		font-weight: 500;
		margin-left: 10rpx;
	}

	.submit-btn {
		flex: 1;
		margin-left: 20rpx;
		background-color: #2979ff;
		color: #fff;
		border-radius: 50rpx;
		font-size: 16px;
		height: 80rpx;
		line-height: 80rpx;
	}
</style>