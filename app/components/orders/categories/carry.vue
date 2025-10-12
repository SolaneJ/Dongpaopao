<template>
	<view class="card">
		<u-title>搬运信息</u-title>
		<!-- 物品描述 -->
		<view class="input-group">
			<text class="input-label">物品描述</text>
			<textarea v-model="businessInfo.description" placeholder="请描述需要搬运的物品（如：家具、电器等）" class="textarea-field" />
		</view>
		<!-- 物品重量 -->
		<view class="input-group">
			<text class="input-label">预估重量</text>
			<picker mode="selector" :range="weightOptions" @change="handleWeightChange">
				<view class="picker">
					{{businessInfo.weight || '请选择物品重量'}}
					<uni-icons type="arrowdown" size="16" color="#999" />
				</view>
			</picker>
		</view>
		<!-- 物品数量 -->
		<view class="input-group">
			<text class="input-label">物品数量</text>
			<view class="counter-wrapper">
				<uni-icons type="minus" size="20" color="#999" @click="decreaseCount" />
				<text class="counter-number">{{businessInfo.count}}</text>
				<uni-icons type="plus" size="20" color="#999" @click="increaseCount" />
			</view>
		</view>
		<!-- 备注 -->
		<view class="input-group">
			<text class="input-label">备注信息</text>
			<textarea v-model="businessInfo.remark" placeholder="可填写搬运相关的特殊要求（选填）" class="textarea-field" />
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { reactive } from 'vue';

	// 搬运信息 - 使用统一的 businessInfo 命名
	const businessInfo = reactive({
		description: '',
		weight: '',
		count: 1,
		remark: ''
	});

	const weightOptions = [
		'10kg以下',
		'10-30kg',
		'30-50kg',
		'50-100kg',
		'100kg以上'
	];

	const handleWeightChange = (e : any) => {
		businessInfo.weight = weightOptions[e.detail.value];
	};

	const increaseCount = () => {
		if (businessInfo.count < 10) {
			businessInfo.count++;
		}
	};

	const decreaseCount = () => {
		if (businessInfo.count > 1) {
			businessInfo.count--;
		}
	};

	// 暴露业务数据给父组件（统一接口）
	const getBusinessData = () => {
		return { ...businessInfo };
	};

	// 验证业务表单
	const validateForm = () => {
		if (!businessInfo.description) {
			uni.showToast({ title: '请输入物品描述', icon: 'none' });
			return false;
		}

		if (!businessInfo.weight) {
			uni.showToast({ title: '请选择预估重量', icon: 'none' });
			return false;
		}

		return true;
	};

	// 暴露方法给父组件（统一接口）
	defineExpose({
		getBusinessData,
		validateForm
	});
</script>

<style scoped>
	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.input-group {
		margin-bottom: 40rpx;
	}

	.counter-wrapper {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		padding: 10rpx 0;
		border-bottom: 1px solid #eee;
	}

	.counter-number {
		margin: 0 30rpx;
		font-size: 16px;
		color: #333;
		min-width: 40rpx;
		text-align: center;
	}

	.toggle-wrapper {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10rpx 0;
		border-bottom: 1px solid #eee;
	}

	.toggle-label {
		font-size: 14px;
		color: #666;
	}

	.input-label {
		display: block;
		font-size: 14px;
		color: #666;
		margin-bottom: 15rpx;
	}

	.textarea-field {
		width: 100%;
		height: 120rpx;
		font-size: 16px;
		border: 1px solid #eee;
		border-radius: 8rpx;
		padding: 15rpx;
		box-sizing: border-box;
	}

	.picker {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 16px;
		color: #333;
		height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}
</style>