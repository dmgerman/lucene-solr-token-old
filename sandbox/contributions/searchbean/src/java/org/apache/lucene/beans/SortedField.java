begin_unit
begin_comment
comment|/*  * SortedField.java  *  * Created on May 20, 2002, 4:15 PM  */
end_comment
begin_package
DECL|package|org.apache.lucene.beans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|beans
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  *  * @author  carlson  */
end_comment
begin_class
DECL|class|SortedField
specifier|public
class|class
name|SortedField
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldValues
specifier|private
name|String
index|[]
name|fieldValues
decl_stmt|;
DECL|field|fieldList
specifier|private
specifier|static
name|Hashtable
name|fieldList
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|//keeps track of all fields
comment|/** Creates a new instance of SortedField */
DECL|method|SortedField
specifier|public
name|SortedField
parameter_list|()
block|{     }
comment|/** add a field so that is can be used to sort      * @param fieldName the name of the field to add      * @param indexPath path to Lucene index directory      */
DECL|method|addField
specifier|public
specifier|static
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|addField
argument_list|(
name|fieldName
argument_list|,
name|ir
argument_list|)
expr_stmt|;
block|}
comment|/** add a field so that is can be used to sort      * @param fieldName the name of the field to add      * @param indexFile File pointing to Lucene index directory      */
DECL|method|addField
specifier|public
specifier|static
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|File
name|indexFile
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexFile
argument_list|)
decl_stmt|;
name|addField
argument_list|(
name|fieldName
argument_list|,
name|ir
argument_list|)
expr_stmt|;
block|}
comment|/** add a field so that is can be used to sort      * @param fieldName the name of the field to add      * @param directory Lucene Directory      */
DECL|method|addField
specifier|public
specifier|static
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|addField
argument_list|(
name|fieldName
argument_list|,
name|ir
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|private
specifier|static
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedField
name|sortedField
init|=
operator|new
name|SortedField
argument_list|()
decl_stmt|;
name|sortedField
operator|.
name|addSortedField
argument_list|(
name|fieldName
argument_list|,
name|ir
argument_list|)
expr_stmt|;
comment|//long start = System.currentTimeMillis();
name|fieldList
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|sortedField
argument_list|)
expr_stmt|;
comment|//logger.info("adding data from field "+fieldName+" took "+(System.currentTimeMillis()-start));
block|}
comment|/** adds the data from the index into a string array      */
DECL|method|addSortedField
specifier|private
name|void
name|addSortedField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
name|ir
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|fieldValues
operator|=
operator|new
name|String
index|[
name|numDocs
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ir
operator|.
name|isDeleted
argument_list|(
name|i
argument_list|)
operator|==
literal|false
condition|)
block|{
name|fieldValues
index|[
name|i
index|]
operator|=
name|ir
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldValues
index|[
name|i
index|]
operator|=
literal|""
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** returns the value of the field      * @param globalID Lucene's global document ID      * @return value of field      */
DECL|method|getFieldValue
specifier|public
name|String
name|getFieldValue
parameter_list|(
name|int
name|globalID
parameter_list|)
block|{
return|return
name|fieldValues
index|[
name|globalID
index|]
return|;
block|}
comment|/** provides way to retrieve a SortedField once you add it      * @param fieldName name of field to lookup      * @return SortedField field to use when sorting      */
DECL|method|getSortedField
specifier|public
specifier|static
name|SortedField
name|getSortedField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|(
name|SortedField
operator|)
name|fieldList
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/** Getter for property fieldName.      * @return Value of property fieldName.      */
DECL|method|getFieldName
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
block|}
end_class
end_unit
