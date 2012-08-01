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
name|Among
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
name|SnowballProgram
import|;
end_import
begin_comment
comment|/**   * This class was automatically generated by a Snowball to Java compiler    * It implements the stemming algorithm defined by a snowball script.   */
end_comment
begin_class
DECL|class|SwedishStemmer
specifier|public
class|class
name|SwedishStemmer
extends|extends
name|SnowballProgram
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|methodObject
specifier|private
specifier|final
specifier|static
name|SwedishStemmer
name|methodObject
init|=
operator|new
name|SwedishStemmer
argument_list|()
decl_stmt|;
DECL|field|a_0
specifier|private
specifier|final
specifier|static
name|Among
name|a_0
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"a"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arna"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erna"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heterna"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"orna"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ad"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ade"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ande"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arne"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"are"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aste"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"anden"
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aren"
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heten"
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ern"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ar"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heter"
argument_list|,
literal|18
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"or"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"as"
argument_list|,
literal|21
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arnas"
argument_list|,
literal|22
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ernas"
argument_list|,
literal|22
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ornas"
argument_list|,
literal|22
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"es"
argument_list|,
literal|21
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ades"
argument_list|,
literal|26
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"andes"
argument_list|,
literal|26
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ens"
argument_list|,
literal|21
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arens"
argument_list|,
literal|29
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"hetens"
argument_list|,
literal|29
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"erns"
argument_list|,
literal|21
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"at"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"andet"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"het"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ast"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|a_1
specifier|private
specifier|final
specifier|static
name|Among
name|a_1
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"dd"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"nn"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
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
name|methodObject
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
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"tt"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|a_2
specifier|private
specifier|final
specifier|static
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
name|methodObject
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
name|methodObject
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
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"fullt"
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"l\u00F6st"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
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
literal|24
block|,
literal|0
block|,
literal|32
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
literal|119
block|,
literal|127
block|,
literal|149
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
DECL|method|copy_from
specifier|private
name|void
name|copy_from
parameter_list|(
name|SwedishStemmer
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
comment|// (, line 26
name|I_p1
operator|=
name|limit
expr_stmt|;
comment|// test, line 29
name|v_1
operator|=
name|cursor
expr_stmt|;
comment|// (, line 29
comment|// hop, line 29
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
comment|// setmark x, line 29
name|I_x
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// goto, line 30
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
literal|246
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
comment|// gopast, line 30
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
literal|246
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
comment|// setmark p1, line 30
name|I_p1
operator|=
name|cursor
expr_stmt|;
comment|// try, line 31
name|lab4
label|:
do|do
block|{
comment|// (, line 31
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
comment|// (, line 36
comment|// setlimit, line 37
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 37
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
comment|// (, line 37
comment|// [, line 37
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 37
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_0
argument_list|,
literal|37
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
comment|// ], line 37
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
comment|// (, line 44
comment|// delete, line 44
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 46
if|if
condition|(
operator|!
operator|(
name|in_grouping_b
argument_list|(
name|g_s_ending
argument_list|,
literal|98
argument_list|,
literal|121
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 46
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
comment|// setlimit, line 50
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 50
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
comment|// (, line 50
comment|// and, line 52
name|v_3
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// among, line 51
if|if
condition|(
name|find_among_b
argument_list|(
name|a_1
argument_list|,
literal|7
argument_list|)
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
name|cursor
operator|=
name|limit
operator|-
name|v_3
expr_stmt|;
comment|// (, line 52
comment|// [, line 52
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// next, line 52
if|if
condition|(
name|cursor
operator|<=
name|limit_backward
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
name|cursor
operator|--
expr_stmt|;
comment|// ], line 52
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// delete, line 52
name|slice_del
argument_list|()
expr_stmt|;
name|limit_backward
operator|=
name|v_2
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
comment|// setlimit, line 55
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
comment|// tomark, line 55
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
comment|// (, line 55
comment|// [, line 56
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 56
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
name|v_2
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
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
name|limit_backward
operator|=
name|v_2
expr_stmt|;
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 57
comment|// delete, line 57
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 58
comment|//<-, line 58
name|slice_from
argument_list|(
literal|"l\u00F6s"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// (, line 59
comment|//<-, line 59
name|slice_from
argument_list|(
literal|"full"
argument_list|)
expr_stmt|;
break|break;
block|}
name|limit_backward
operator|=
name|v_2
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
comment|// (, line 64
comment|// do, line 66
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// call mark_regions, line 66
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
comment|// backwards, line 67
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
expr_stmt|;
comment|// (, line 67
comment|// do, line 68
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
comment|// call main_suffix, line 68
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
comment|// do, line 69
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
comment|// call consonant_pair, line 69
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
comment|// do, line 70
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
comment|// call other_suffix, line 70
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
name|cursor
operator|=
name|limit_backward
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|SwedishStemmer
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|SwedishStemmer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
