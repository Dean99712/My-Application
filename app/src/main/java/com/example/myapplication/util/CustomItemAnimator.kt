import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CustomItemAnimator : DefaultItemAnimator() {
    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {

        // now let's customize the remove item animation
        // it's the same operation that we did before
        // but we need another custom animation. let's create it ..
        holder.itemView.animation = AnimationUtils.loadAnimation(
            holder.itemView.context,
            R.anim.remove_item
        )
        return super.animateRemove(holder)
    }


    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {

        // this method will be called when an new item will be added to the list
        // we will handle the add animation to the item here
        // first let's create a custom animation ...
        // now let's apply the animation to the viewholder
        holder.itemView.animation = AnimationUtils.loadAnimation(
            holder.itemView.context,
            R.anim.add_item
        )
        return super.animateAdd(holder)
    }

    // we can also customize the duration of the add animation
    override fun getAddDuration(): Long {

        // as you have seen the default duration is 120 ms
        // i change it to 500
        return 500
    }

    override fun getRemoveDuration(): Long {
        return 500
    }
}
