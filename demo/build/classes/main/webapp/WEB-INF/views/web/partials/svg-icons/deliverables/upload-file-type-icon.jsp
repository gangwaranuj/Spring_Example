<svg version="1.1"  class="deliverables-live-icon icon-position file-type-icon" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
		 viewBox="0 0 29 29" enable-background="new 0 0 29 29" xml:space="preserve">
	<g>
		<path fill="#949899" d="M16.744,1C18.264,1,23,6.097,23,7.733v12.433C23,21.729,21.729,23,20.166,23H3.835
				C2.272,23,1,21.729,1,20.166V3.834C1,2.271,2.272,1,3.835,1H16.744 M16.744,0H3.835C1.721,0,0,1.72,0,3.834v16.331
				C0,22.28,1.721,24,3.835,24h16.331C22.281,24,24,22.28,24,20.166V7.733C24,5.565,18.779,0,16.744,0L16.744,0z"/>
		<g opacity="0.3">
			<path d="M16.318,0.687v3.96c0,2.047,1.66,3.707,3.707,3.707H23.5"/>
			<path d="M23.5,8.541h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.688h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519H23.5V8.541z"/>
		</g>
		<path fill="#FFFFFF" d="M16.818,0.187v3.96c0,2.047,1.66,3.707,3.707,3.707H24"/>
		<path fill="#4C5355" d="M24,8.041h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.188h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519
					H24V8.041z"/>
		<text fill="#4C5355" font-weight="600" font-family="Open Sans"
			{{ if (!_.isUndefined(asset)) { }}
				{{ if (_.size(asset.extension) === 3) { }}
					{{ if (asset.extension === "zip") { }}
							x="4.5"
					{{ } else if (asset.extension === "gif") { }}
							x="4"
					{{ } else if (asset.extension === "txt") { }}
							x="3.4"
					{{ } else if (asset.extension === "png" || asset.extension === "bmp") { }}
							x="2.7"
					{{ } else if (asset.extension === "pdf" || asset.extension === "xls" || asset.extension === "f4v" || asset.extension === "f4a" || asset.extension === "jpg") { }}
							x="3"
					{{ } else if (asset.extension === "mov") { }}
							x="1.5"
					{{ } else if (asset.extension === "mp4" || asset.extension === "m4v" || asset.extension === "m4a" || asset.extension === "mp3") { }}
							x="2.5"
					{{ } else if (asset.extension === "flv") { }}
							x="3.5"
					{{ } else { }}
							x="2.3"
					{{ } }}
					y="17.5" font-size="7pt"
				{{ } else { }}
					{{ if (asset.extension === "xlsx" || asset.extension === "jpeg") { }}
								x="3.5"
					{{ } else if (asset.extension === "docx") { }}
								x="2"
					{{ } else if (asset.extension === "tiff") { }}
								x="5"
					{{ } else { }}
								x="3"
					{{ } }}
						y="17" font-size="5pt"
				{{ } }} >{{= asset.extension.toUpperCase() }}
				{{ } }}</text>
	</g>
</svg>
