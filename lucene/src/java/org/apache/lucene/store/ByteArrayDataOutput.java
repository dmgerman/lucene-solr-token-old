begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ByteArrayDataOutput
specifier|public
class|class
name|ByteArrayDataOutput
extends|extends
name|DataOutput
block|{
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|method|ByteArrayDataOutput
specifier|public
name|ByteArrayDataOutput
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|reset
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteArrayDataOutput
specifier|public
name|ByteArrayDataOutput
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|reset
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteArrayDataOutput
specifier|public
name|ByteArrayDataOutput
parameter_list|()
block|{
name|reset
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|reset
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|pos
operator|=
name|offset
expr_stmt|;
name|limit
operator|=
name|offset
operator|+
name|len
expr_stmt|;
block|}
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
assert|assert
name|pos
operator|<
name|limit
assert|;
name|bytes
index|[
name|pos
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
assert|assert
name|pos
operator|+
name|length
operator|<=
name|limit
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|bytes
argument_list|,
name|pos
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|length
expr_stmt|;
block|}
block|}
end_class
end_unit
