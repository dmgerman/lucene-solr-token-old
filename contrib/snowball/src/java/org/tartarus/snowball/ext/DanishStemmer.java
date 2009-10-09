begin_unit
begin_comment
comment|// This file was generated automatically by the Snowball to Java compiler
end_comment
begin_package
DECL|package|org.tartarus.snowball.ext
package|package
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
package|;
end_package
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|Among
import|;
end_import
begin_comment
comment|/**  * Generated class implementing code defined by a snowball script.  */
end_comment
begin_class
DECL|class|DanishStemmer
specifier|public
class|class
name|DanishStemmer
extends|extends
name|SnowballProgram
block|{
DECL|field|a_0
specifier|private
name|Among
name|a_0
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"hed"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ethed"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ered"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"e"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erede"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ende"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erende"
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ene"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erne"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ere"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"en"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heden"
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eren"
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"er"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heder"
argument_list|,
literal|13
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erer"
argument_list|,
literal|13
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"s"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heds"
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"es"
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"endes"
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erendes"
argument_list|,
literal|19
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"enes"
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ernes"
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eres"
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ens"
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"hedens"
argument_list|,
literal|24
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erens"
argument_list|,
literal|24
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ers"
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ets"
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erets"
argument_list|,
literal|28
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"et"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eret"
argument_list|,
literal|30
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_1
specifier|private
name|Among
name|a_1
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"gd"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"dt"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"gt"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"kt"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_2
specifier|private
name|Among
name|a_2
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"ig"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"lig"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"elig"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"els"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"l\u00F8st"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|g_v
specifier|private
specifier|static
specifier|final
name|char
name|g_v
index|[]
init|=
block|{
literal|17
block|,
literal|65
block|,
literal|16
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|48
block|,
literal|0
block|,
literal|128
block|}
decl_stmt|;
DECL|field|g_s_ending
specifier|private
specifier|static
specifier|final
name|char
name|g_s_ending
index|[]
init|=
block|{
literal|239
block|,
literal|254
block|,
literal|42
block|,
literal|3
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|16
block|}
decl_stmt|;
DECL|field|I_x
specifier|private
name|int
name|I_x
decl_stmt|;
DECL|field|I_p1
specifier|private
name|int
name|I_p1
decl_stmt|;
DECL|field|S_ch
specifier|private
name|StringBuilder
name|S_ch
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|method|copy_from
specifier|private
name|void
name|copy_from
parameter_list|(
name|DanishStemmer
name|other
parameter_list|)
block|{
name|I_x
operator|=
name|other
operator|.
name|I_x
expr_stmt|;
name|I_p1
operator|=
name|other
operator|.
name|I_p1
expr_stmt|;
name|S_ch
operator|=
name|other
operator|.
name|S_ch
expr_stmt|;
name|super
operator|.
name|copy_from
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
DECL|method|r_mark_regions
specifier|private
name|boolean
name|r_mark_regions
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
comment|// (, line 29
name|I_p1
operator|=
name|limit
expr_stmt|;
comment|// test, line 33
name|v_1
operator|=
name|cursor
expr_stmt|;
comment|// (, line 33
comment|// hop, line 33
block|{
name|int
name|c
init|=
name|cursor
operator|+
literal|3
decl_stmt|;
if|if
condition|(
literal|0
operator|>
name|c
operator|||
name|c
operator|>
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|c
expr_stmt|;
block|}
comment|// setmark x, line 33
name|I_x
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// goto, line 34
name|golab0
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|v_2
operator|=
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|248
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab1
break|;
block|}
name|cursor
operator|=
name|v_2
expr_stmt|;
break|break
name|golab0
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_2
expr_stmt|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// gopast, line 34
name|golab2
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab3
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|out_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|248
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab3
break|;
block|}
break|break
name|golab2
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark p1, line 34
name|I_p1
operator|=
name|cursor
expr_stmt|;
comment|// try, line 35
name|lab4
label|:
do|do
block|{
comment|// (, line 35
if|if
condition|(
operator|!
operator|(
name|I_p1
operator|<
name|I_x
operator|)
condition|)
block|{
break|break
name|lab4
break|;
block|}
name|I_p1
operator|=
name|I_x
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
return|return
literal|true
return|;
block|}
DECL|method|r_main_suffix
specifier|private
name|boolean
name|r_main_suffix
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
comment|// (, line 40
comment|// setlimit, line 41
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 41
if|if
condition|(
name|cursor
operator|<
name|I_p1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|I_p1
expr_stmt|;
name|v_2
operator|=
name|limit_backward
expr_stmt|;
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
operator|-
name|v_1
expr_stmt|;
comment|// (, line 41
comment|// [, line 41
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 41
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_0
argument_list|,
literal|32
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
name|limit_backward
operator|=
name|v_2
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// ], line 41
name|bra
operator|=
name|cursor
expr_stmt|;
name|limit_backward
operator|=
name|v_2
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 48
comment|// delete, line 48
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 50
if|if
condition|(
operator|!
operator|(
name|in_grouping_b
argument_list|(
name|g_s_ending
argument_list|,
literal|97
argument_list|,
literal|229
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 50
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_consonant_pair
specifier|private
name|boolean
name|r_consonant_pair
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
comment|// (, line 54
comment|// test, line 55
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// (, line 55
comment|// setlimit, line 56
name|v_2
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 56
if|if
condition|(
name|cursor
operator|<
name|I_p1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|I_p1
expr_stmt|;
name|v_3
operator|=
name|limit_backward
expr_stmt|;
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
operator|-
name|v_2
expr_stmt|;
comment|// (, line 56
comment|// [, line 56
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 56
if|if
condition|(
name|find_among_b
argument_list|(
name|a_1
argument_list|,
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
name|limit_backward
operator|=
name|v_3
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// ], line 56
name|bra
operator|=
name|cursor
expr_stmt|;
name|limit_backward
operator|=
name|v_3
expr_stmt|;
name|cursor
operator|=
name|limit
operator|-
name|v_1
expr_stmt|;
comment|// next, line 62
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|--
expr_stmt|;
comment|// ], line 62
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// delete, line 62
name|slice_del
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|r_other_suffix
specifier|private
name|boolean
name|r_other_suffix
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
comment|// (, line 65
comment|// do, line 66
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// (, line 66
comment|// [, line 66
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// literal, line 66
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|2
argument_list|,
literal|"st"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab0
break|;
block|}
comment|// ], line 66
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// literal, line 66
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|2
argument_list|,
literal|"ig"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab0
break|;
block|}
comment|// delete, line 66
name|slice_del
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_1
expr_stmt|;
comment|// setlimit, line 67
name|v_2
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 67
if|if
condition|(
name|cursor
operator|<
name|I_p1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|I_p1
expr_stmt|;
name|v_3
operator|=
name|limit_backward
expr_stmt|;
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
operator|-
name|v_2
expr_stmt|;
comment|// (, line 67
comment|// [, line 67
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 67
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_2
argument_list|,
literal|5
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
name|limit_backward
operator|=
name|v_3
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// ], line 67
name|bra
operator|=
name|cursor
expr_stmt|;
name|limit_backward
operator|=
name|v_3
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 70
comment|// delete, line 70
name|slice_del
argument_list|()
expr_stmt|;
comment|// do, line 70
name|v_4
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// call consonant_pair, line 70
if|if
condition|(
operator|!
name|r_consonant_pair
argument_list|()
condition|)
block|{
break|break
name|lab1
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 72
comment|//<-, line 72
name|slice_from
argument_list|(
literal|"l\u00F8s"
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_undouble
specifier|private
name|boolean
name|r_undouble
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
comment|// (, line 75
comment|// setlimit, line 76
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 76
if|if
condition|(
name|cursor
operator|<
name|I_p1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|I_p1
expr_stmt|;
name|v_2
operator|=
name|limit_backward
expr_stmt|;
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
operator|-
name|v_1
expr_stmt|;
comment|// (, line 76
comment|// [, line 76
name|ket
operator|=
name|cursor
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|out_grouping_b
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|248
argument_list|)
operator|)
condition|)
block|{
name|limit_backward
operator|=
name|v_2
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// ], line 76
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// -> ch, line 76
name|S_ch
operator|=
name|slice_to
argument_list|(
name|S_ch
argument_list|)
expr_stmt|;
name|limit_backward
operator|=
name|v_2
expr_stmt|;
comment|// name ch, line 77
if|if
condition|(
operator|!
operator|(
name|eq_v_b
argument_list|(
name|S_ch
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 78
name|slice_del
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
name|int
name|v_5
decl_stmt|;
comment|// (, line 82
comment|// do, line 84
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// call mark_regions, line 84
if|if
condition|(
operator|!
name|r_mark_regions
argument_list|()
condition|)
block|{
break|break
name|lab0
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// backwards, line 85
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
expr_stmt|;
comment|// (, line 85
comment|// do, line 86
name|v_2
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// call main_suffix, line 86
if|if
condition|(
operator|!
name|r_main_suffix
argument_list|()
condition|)
block|{
break|break
name|lab1
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_2
expr_stmt|;
comment|// do, line 87
name|v_3
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab2
label|:
do|do
block|{
comment|// call consonant_pair, line 87
if|if
condition|(
operator|!
name|r_consonant_pair
argument_list|()
condition|)
block|{
break|break
name|lab2
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_3
expr_stmt|;
comment|// do, line 88
name|v_4
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// call other_suffix, line 88
if|if
condition|(
operator|!
name|r_other_suffix
argument_list|()
condition|)
block|{
break|break
name|lab3
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
comment|// do, line 89
name|v_5
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab4
label|:
do|do
block|{
comment|// call undouble, line 89
if|if
condition|(
operator|!
name|r_undouble
argument_list|()
condition|)
block|{
break|break
name|lab4
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_5
expr_stmt|;
name|cursor
operator|=
name|limit_backward
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
