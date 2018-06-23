# Saber

## 本项目帮助你快速使用LiveData与ViewModel

## 详细介绍

- [感受LiveData 与 ViewModel结合之美](https://blog.csdn.net/qq_17766199/article/details/80732836)

## 使用方式

添加依赖

```
    implementation 'com.github.simplezhli.Saber:saber-api:0.1.2'
    annotationProcessor 'com.github.simplezhli.Saber:saber-compiler:0.1.2'
```

首先创建一个类，使用`@LiveData`注解标记你要保存的数据。注意这里的参数名称value，下面会用到。
```
public class SeekBar {
    
    @LiveData
    Integer value;
}
```

Build -- > Make Project 会生成代码如下：

```
public class SeekBarViewModel extends ViewModel {
  private MutableLiveData<Integer> mValue;

  public MutableLiveData<Integer> getValue() {
    if (mValue == null) {
      mValue = new MutableLiveData<>();
    }
    return mValue;
  }

  public Integer getValueValue() {
    return getValue().getValue();
  }

  public void setValue(Integer mValue) {
    if (this.mValue == null) {
      return;
    }
    this.mValue.setValue(mValue);
  }

  public void postValue(Integer mValue) {
    if (this.mValue == null) {
      return;
    }
    this.mValue.postValue(mValue);
  }
}

```

生成代码提供了ViewModel的常用操作。

- `setXXX()`要在主线程中调用。

- `postXXX()`既可在主线程也可在子线程中调用。

- `getXXX()`用于添加观察者。

- `getXXXValue()`可以获取保存的数据。

一般情况下可以直接使用它。比如上面的Fragment例子。简化为：

```
public class TestFragment extends Fragment {

    private SeekBar mSeekBar;

    @BindViewModel(isShare = true) //<--标记需要绑定的ViewModel
    SeekBarViewModel mSeekBarViewModel;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        mSeekBar = root.findViewById(R.id.seekBar);
        Saber.bind(this); // <--这里绑定ViewModel
        subscribeSeekBar();
        return root;
    }

    private void subscribeSeekBar() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekBarViewModel.setValue(progress);
                }
            }
			......
        });
    }

    @OnChange(model = "mSeekBarViewModel") //<--接收变化
    void setData(Integer value){ //注意这里使用 @LiveData 标记的参数名
        if (value != null) {
            mSeekBar.setProgress(value);
        }
    }
}
```

`@BindViewModel`用于绑定ViewModel。

`@OnChange(model = "xxx")`用于接收指定ViewModel的数据变化。

如果需要`Fragment`之间数据共享，需要`@BindViewModel(isShare = true)`，当然也要保证传入相同的key值。默认key值是类的规范名称，也就是包名加类名。

![这里写图片描述](https://img-blog.csdn.net/20180619100304754?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE3NzY2MTk5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

所以一旦需要互通的Fragment类名或包名不一致，就无法数据共享。这时可以指定key值：`@BindViewModel(key = "value")`

更多的使用方法可以参看本项目demo。


## Thanks For

- [butterknife](https://github.com/JakeWharton/butterknife)

## License

	Copyright 2018 simplezhli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.