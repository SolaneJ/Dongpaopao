// utils/request.js

const BASE_URL = 'http://localhost:8181/api/' // 你的基础URL

function request(options) {
	const config = {
		url: BASE_URL + options.url,
		method: options.method || 'GET',
		data: options.data || {},
		header: {
			'Content-Type': 'application/json',
			...options.header
		},
		timeout: options.timeout || 10000
	}

	return new Promise((resolve, reject) => {
		uni.request({
			...config,
			success: (res) => {
				resolve(res.data)
			},
			fail: (err) => {
				reject(err)
			}
		})
	})
}

// 默认导出
export default request