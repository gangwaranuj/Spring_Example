export default (options) => {
	if (options.isBuyer) {
		const loadIntroJs = async() => {
			const module = await import(/* webpackChunkName: "IntroJs" */ '../config/introjs');
			return module.default;
		};
		loadIntroJs().then((IntroJs) => {
			const intro = IntroJs('intro-payments-tour');
			intro.setOptions({
				steps: [
					{
						intro: "<h4>Payments made simple</h4><p>Work Market makes payments simple by showing how much you owe (current payables), how much you have on your account to make payments (cash balance) and how much you have left to spend (available to spend).</p><p>Real-world payment cycles allow you to pay workers when work is completed to your satisfaction. <a href='/funds/accounts/new' target='_blank'>Connect a bank account</a> and configure your payment terms so you have the ability to send assignments now without pre-funding your account.</p><p>If you choose not to connect your bank account, you will need to pre-fund your account via a wire transaction, check or credit card (fees may apply).</p>"
					}
				]
			});

			intro.watchOnce();
		});
	}
};
