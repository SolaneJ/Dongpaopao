<template>
	<view class="card">
		<u-title>外卖信息</u-title>
		<!-- 取件码和取件码上传 -->
		<view class="input-group">
			<text class="input-label">取件码</text>
			<view class="input-wrapper">
				<input v-model="businessInfo.code" placeholder="请输入取件码" class="input-field" />
				<uni-icons type="scan" size="20" color="#999" @click="handleScan" />
			</view>
		</view>
		<!-- 取餐点 -->
		<view class="input-group">
			<text class="input-label">取餐点</text>
			<picker mode="selector" :range="pickupLocations" @change="handlePickupLocationChange">
				<view class="picker">
					{{businessInfo.location || '请选择取餐点'}}
					<uni-icons type="arrowdown" size="16" color="#999" />
				</view>
			</picker>
		</view>
		<!-- 备注 -->
		<view class="input-group">
			<text class="input-label">备注信息</text>
			<textarea v-model="businessInfo.remark" placeholder="可填写餐品相关的特殊要求（选填）" class="textarea-field" />
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, reactive } from 'vue';

	// 外卖信息 - 使用统一的 businessInfo 命名
	const businessInfo = reactive({
		code: '',
		location: '',
		remark: ''
	});

	// 取餐点列表
	const pickupLocations = ref([
		'校门口外卖架',
		'宿舍楼下外卖点',
		'教学楼外卖存放处',
		'食堂外卖领取点',
		'其他指定位置'
	]);

	const handleScan = () => {
		uni.scanCode({
			success: (res) => {
				businessInfo.code = res.result;
			}
		});
	};

	const handlePickupLocationChange = (e : any) => {
		businessInfo.location = pickupLocations.value[e.detail.value];
	};

	// 暴露业务数据给父组件（统一接口）
	const getBusinessData = () => {
		return { ...businessInfo };
	};

	// 验证业务表单
	const validateForm = () => {
		if (!businessInfo.code) {
			uni.showToast({ title: '请输入取件码', icon: 'none' });
			return false;
		}

		if (!businessInfo.location) {
			uni.showToast({ title: '请选择取餐点', icon: 'none' });
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

	.input-label {
		display: block;
		font-size: 14px;
		color: #666;
		margin-bottom: 15rpx;
	}

	.input-wrapper {
		display: flex;
		align-items: center;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}

	.input-field {
		flex: 1;
		font-size: 16px;
		height: 50rpx;
		line-height: 50rpx;
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