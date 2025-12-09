	ORG	$0100
uno	equ	$30
dos	equ	$505
lbl1	dc.b	"ABC"
	ldy 	#lbl1
	ldx	uno
et1	ldaa	dos
	ldab	lbl1
	end
	
	